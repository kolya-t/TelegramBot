package kolyat.telegram;

import kolyat.telegram.domain.ChatWeather;
import kolyat.telegram.repository.ChatWeatherRepository;
import kolyat.telegram.service.WeatherService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.bots.commandbot.commands.BotCommand;

@Slf4j
@Component
public class Bot extends TelegramLongPollingCommandBot {
    @Getter
    @Value("${kolyat.telegram-bot.bot-token}")
    private String botToken;

    @Autowired
    private ChatWeatherRepository chatWeatherRepository;

    @Autowired
    private WeatherService weatherService;

    public Bot(@Value("${kolyat.telegram-bot.bot-username}") String botUsername, BotCommand[] commands) {
        super(botUsername);
        registerAll(commands);
    }

    //    @Scheduled(cron = "0 0/30 6 * * *")
    @Scheduled(cron = "*/3 * * * * *")
    public void sendForecastForTodayToAll() {
        for (ChatWeather chatWeather : chatWeatherRepository.findAllBySubscribed(true)) {
            weatherService.sendForecastForToday(this, chatWeather);
        }
    }

    @Override
    public void processNonCommandUpdate(Update update) {

    }
}
