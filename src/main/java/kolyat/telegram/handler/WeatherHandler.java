package kolyat.telegram.handler;

import com.github.fedy2.weather.YahooWeatherService;
import com.github.fedy2.weather.data.Channel;
import com.github.fedy2.weather.data.Forecast;
import com.github.fedy2.weather.data.unit.DegreeUnit;
import kolyat.telegram.domain.WeatherCity;
import kolyat.telegram.repository.WeatherCityRepository;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Location;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.Collections;

import static java.lang.String.format;

@Slf4j
@Component
public class WeatherHandler extends AbstractHandler {
    private static final String SUBSCRIBE_FOR_SCHEDULING_TEXT = "\uD83D\uDD62 Подписаться на прогноз погоды каждый день в 6:30";
    private static final String SIX_THIRTY_CRON = "0 0/30 6 * * *";

    @Autowired
    private YahooWeatherService weatherService;

    @Autowired
    private WeatherCityRepository weatherCityRepository;

    @Autowired
    private TaskScheduler taskScheduler;

    @PostConstruct
    public void postConstructorSchedule() {
        weatherCityRepository.findAllByIsEnabledScheduling(true).forEach(this::schedule);
    }

    private void schedule(WeatherCity weatherCity) {
        taskScheduler.schedule(() -> sendDailyForecast(weatherCity), new CronTrigger(SIX_THIRTY_CRON));
    }

    private void sendDailyForecast(WeatherCity weatherCity) {
        try {
            Channel forecast = getForecastChannelForLocation(weatherCity.getLocation());
            Forecast today = forecast.getItem().getForecasts().get(0);
            String message = String.format("Сегодня будет\n*%d .. %d°C*", today.getLow(), today.getHigh());
            execute(new SendMessage(weatherCity.getChatId(), message).enableMarkdown(true));
        } catch (TelegramApiException e) {
            log.error("Error sending message", e);
        }
    }

    @Override
    @SneakyThrows
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            if (message.hasText()) {
                String text = message.getText();
                if (text.equals("/start")) {
                    // todo: проверить что будет если уже зарегистрирован
                    KeyboardButton button = new KeyboardButton()
                            .setText("\uD83C\uDF0E Отправить геопозицию")
                            .setRequestLocation(true);

                    KeyboardRow row = new KeyboardRow();
                    row.add(button);

                    ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup()
                            .setKeyboard(Collections.singletonList(row))
                            .setResizeKeyboard(true)
                            .setOneTimeKeyboard(true);

                    execute(new SendMessage()
                            .setChatId(message.getChatId())
                            .setReplyMarkup(keyboardMarkup)
                            .setText("Я буду узнавать для вас прогноз погоды. " +
                                    "Отправьте мне вашу геопозицию, чтобы я мог начать."));
                } else if (text.equals("/end")) {
                    WeatherCity weatherCity = weatherCityRepository.findByChatId(message.getChatId());
                    if (weatherCity != null) {
                        weatherCityRepository.delete(weatherCity);
                        execute(new SendMessage()
                                .setChatId(message.getChatId())
                                .setReplyMarkup(new ReplyKeyboardRemove())
                                .setText("Удаляю все данные о вас из базы. Больше не буду присылать вам погоду."));
                    }
                } else if (text.equals(SUBSCRIBE_FOR_SCHEDULING_TEXT)) {
                    WeatherCity weatherCity = weatherCityRepository.findByChatId(message.getChatId());
                    if (weatherCity != null && !weatherCity.getIsEnabledScheduling()) {
                        schedule(weatherCity);
                        weatherCity.setIsEnabledScheduling(true);
                        weatherCityRepository.save(weatherCity);
                        execute(new SendMessage()
                                .setChatId(message.getChatId())
                                .setText("Я буду присылать вам прогноз погоды каждый день в 6:30")
                                .setReplyMarkup(new ReplyKeyboardRemove()));
                    }
                    //todo: если уже зарегистрирован
                }
            } else if (message.hasLocation() && !weatherCityRepository.existsByChatId(message.getChatId())) {
                Location location = message.getLocation();
                Channel channel = getForecastChannelForLocation(location);
                if (channel != null) {
                    KeyboardRow row = new KeyboardRow();
                    row.add(new KeyboardButton(SUBSCRIBE_FOR_SCHEDULING_TEXT));

                    ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup()
                            .setKeyboard(Collections.singletonList(row))
                            .setResizeKeyboard(true)
                            .setOneTimeKeyboard(true);

                    execute(new SendMessage()
                            .setChatId(message.getChatId())
                            .enableMarkdown(true)
                            .setReplyMarkup(keyboardMarkup)
                            .setText(format("Буду получать данные о погоде для *%s, %s, %s*. " +
                                            "Теперь можете разрешить мне присылать вам прогноз погоды в 6:30.",
                                    channel.getLocation().getCity(),
                                    channel.getLocation().getRegion(),
                                    channel.getLocation().getCountry())));

                    weatherCityRepository.save(new WeatherCity(message.getChatId(), location));
                } else {
                    new SendMessage().setText("Не могу получить данные о погоде по вашей геопозиции");
                }
            }
        }
    }

    private Channel getForecastChannelForLocation(Location location) {
        Channel channel = null;
        String coordinates = String.format("(%s,%s)", location.getLatitude(), location.getLongitude());
        try {
            channel = weatherService.getForecastForLocation(coordinates, DegreeUnit.CELSIUS)
                    .first(1)
                    .get(0);
        } catch (JAXBException | IOException | IndexOutOfBoundsException e) {
            log.error(format("Unable to get forecast for %s", coordinates), e);
        }
        return channel;
    }

    @Configuration
    class Config {
        @Bean
        public YahooWeatherService weatherService() throws JAXBException {
            return new YahooWeatherService();
        }
    }
}
