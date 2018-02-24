package kolyat.telegram;

import com.github.fedy2.weather.data.Channel;
import kolyat.telegram.domain.ChatWeather;
import kolyat.telegram.repository.ChatWeatherRepository;
import kolyat.telegram.service.WeatherService;
import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.Location;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.Collections;

@Component
public class WeatherBot extends TelegramLongPollingCommandBot {
    private static final String WEATHER_FOR_NOW = "☂ Погода сейчас";

    @Getter
    @Value("${kolyat.telegram-bot.bot-token}")
    private String botToken;

    @Autowired
    private ChatWeatherRepository chatWeatherRepository;

    @Autowired
    private WeatherService weatherService;

    public WeatherBot(@Value("${kolyat.telegram-bot.bot-username}") String botUsername, BotCommand[] commands) {
        super(botUsername);
        registerAll(commands);
    }

    public static ReplyKeyboard createKeyboardMarkup(boolean withWeatherForNow, boolean withGeolocation) {
        KeyboardRow row = new KeyboardRow();
        if (withWeatherForNow) {
            row.add(new KeyboardButton(WEATHER_FOR_NOW));
        }
        if (withGeolocation) {
            row.add(new KeyboardButton("\uD83C\uDF0E Отправить геопозицию")
                    .setRequestLocation(true));
        }

        return !withWeatherForNow && !withGeolocation ?
                new ReplyKeyboardRemove() :
                new ReplyKeyboardMarkup()
                        .setKeyboard(Collections.singletonList(row))
                        .setResizeKeyboard(true)
                        .setOneTimeKeyboard(true);
    }

    @Scheduled(cron = "0 0/30 6 * * *")
    public void sendForecastForTodayToAll() {
        for (ChatWeather chatWeather : chatWeatherRepository.findAllBySubscribed(true)) {
            weatherService.sendForecastForToday(this, chatWeather);
        }
    }

    @Override
    public void processNonCommandUpdate(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            if (message.hasLocation()) {
                processLocation(message.getLocation(), message.getChat());
            } else if (message.hasText() && message.getText().equals(WEATHER_FOR_NOW)) {
                ChatWeather chatWeather = chatWeatherRepository.findByChatId(message.getChatId());
                if (chatWeather != null) {
                    weatherService.sendForecastForNow(this, chatWeather);
                }
            }
        }
    }

    @SneakyThrows(TelegramApiException.class)
    private void processLocation(Location location, Chat chat) {
        SendMessage answer = new SendMessage()
                .setChatId(chat.getId())
                .setReplyMarkup(new ReplyKeyboardRemove());

        Channel channel = weatherService.getForecastChannelForLocation(location);
        ChatWeather chatWeather = chatWeatherRepository.findByChatId(chat.getId());

        if (chatWeather == null) {
            if (channel != null) {
                chatWeatherRepository.save(new ChatWeather(chat.getId(), location));
                answer.enableMarkdown(true)
                        .setReplyMarkup(createKeyboardMarkup(true, false))
                        .setText(String.format("Буду получать данные о погоде для *%s*, *%s*, *%s*. Теперь можете " +
                                        "разрешить мне присылать вам прогноз погоды в 6:30, выполнив /subscribe",
                                channel.getLocation().getCity(),
                                channel.getLocation().getRegion(),
                                channel.getLocation().getCountry()));
            } else {
                answer.setReplyMarkup(createKeyboardMarkup(false, chat.isUserChat()))
                        .setText("Не могу получить данные о погоде по вашей геопозиции");
            }
        }

        execute(answer);
    }
}
