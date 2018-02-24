package kolyat.telegram.service;

import com.github.fedy2.weather.YahooWeatherService;
import com.github.fedy2.weather.data.Channel;
import com.github.fedy2.weather.data.Forecast;
import com.github.fedy2.weather.data.unit.DegreeUnit;
import kolyat.telegram.domain.ChatWeather;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Location;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import javax.xml.bind.JAXBException;
import java.io.IOException;

@Slf4j
@Service
public class WeatherService {

    @Autowired
    private YahooWeatherService weatherService;

    public void sendForecastForToday(AbsSender absSender, ChatWeather chatWeather) {
        try {
            Channel forecast = getForecastChannelForLocation(chatWeather.getLocation());
            Forecast today = forecast.getItem().getForecasts().get(0);
            String message = String.format("Сегодня будет\n*%d* .. *%d*°C", today.getLow(), today.getHigh());
            absSender.execute(new SendMessage(chatWeather.getChatId(), message).enableMarkdown(true));
        } catch (TelegramApiException e) {
            log.error("Error sending message", e);
        }
    }

    public Channel getForecastChannelForLocation(Location location) {
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
