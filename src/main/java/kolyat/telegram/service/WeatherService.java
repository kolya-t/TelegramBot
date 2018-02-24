package kolyat.telegram.service;

import com.github.fedy2.weather.YahooWeatherService;
import com.github.fedy2.weather.data.Channel;
import com.github.fedy2.weather.data.Forecast;
import com.github.fedy2.weather.data.unit.DegreeUnit;
import kolyat.telegram.domain.ChatWeather;
import kolyat.telegram.repository.ChatWeatherRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.objects.Location;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBException;
import java.io.IOException;

@Slf4j
@Component
public class WeatherService {
    //    private static final String SIX_THIRTY_CRON = "0 0/30 6 * * *";
    private static final String SIX_THIRTY_CRON = "*/3 * * * * *";

    @Autowired
    private ChatWeatherRepository chatWeatherRepository;

    @Autowired
    private YahooWeatherService weatherService;

    @Autowired
    private TaskScheduler taskScheduler;

//    @Autowired
//    private AbsSender absSender;

    @PostConstruct
    public void postConstructorSchedule() {
        chatWeatherRepository.findAllBySubscribed(true).forEach(this::schedule);
    }

    public void schedule(ChatWeather chatWeather) {
        taskScheduler.schedule(() -> sendForecastForToday(chatWeather), new CronTrigger(SIX_THIRTY_CRON));
    }

    private void sendForecastForToday(ChatWeather chatWeather) {
//        try {
        Channel forecast = getForecastChannelForLocation(chatWeather.getLocation());
        Forecast today = forecast.getItem().getForecasts().get(0);
        String message = String.format("Сегодня будет\n*%d .. %d°C*", today.getLow(), today.getHigh());
//            absSender.execute(new SendMessage(chatWeather.getChatId(), message).enableMarkdown(true));
//        } catch (TelegramApiException e) {
//            log.error("Error sending message", e);
//        }
    }

    private Channel getForecastChannelForLocation(Location location) {
        Channel channel = null;
        String coordinates = String.format("(%s,%s)", location.getLatitude(), location.getLongitude());
        try {
            channel = weatherService.getForecastForLocation(coordinates, DegreeUnit.CELSIUS)
                    .first(1)
                    .get(0);
        } catch (JAXBException | IOException | IndexOutOfBoundsException e) {
            log.error(String.format("Unable to get forecast for %s", coordinates), e);
        }
        return channel;
    }

}
