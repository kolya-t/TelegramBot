package ru.kolyat.telegram.handler;

import com.github.fedy2.weather.YahooWeatherService;
import com.github.fedy2.weather.data.Channel;
import com.github.fedy2.weather.data.Forecast;
import com.github.fedy2.weather.data.unit.DegreeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import javax.xml.bind.JAXBException;
import java.io.IOException;

@Slf4j
@Component
public class WeatherHandler extends AbstractHandler {
    @Autowired
    private YahooWeatherService weatherService;

    @Value("${ru.kolyat.telegram.subscriberChatId}")
    private String subscriberChatId;

    @Value("${ru.kolyat.telegram.woeid}")
    private String woeid;

    /**
     * Sends weather forecast for the day to subscriber every day at 6:30 a.m.
     */
    @Scheduled(cron = "0 0/30 6 * * *")
    public void sendWeatherEach5Seconds() {
        try {
            try {
                Channel forecast = weatherService.getForecast(woeid, DegreeUnit.CELSIUS);
                Forecast today = forecast.getItem().getForecasts().get(0);
                String message = String.format("Сегодня будет\n*%d .. %d°C*", today.getLow(), today.getHigh());
                execute(new SendMessage(subscriberChatId, message).enableMarkdown(true));
            } catch (JAXBException | IOException e) {
                log.error("Error retrieving forecast", e);
                execute(new SendMessage(subscriberChatId, "Я пытался получить для вас данные о погоде, " +
                        "но что-то пошло не так. Сообщите об этом @kolya_t"));
            }
        } catch (TelegramApiException e) {
            log.error("Error sending message", e);
        }
    }

    @Configuration
    class Config {
        @Bean
        public YahooWeatherService weatherService() throws JAXBException {
            return new YahooWeatherService();
        }
    }
}
