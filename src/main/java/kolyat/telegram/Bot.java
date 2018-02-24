package kolyat.telegram;

import kolyat.telegram.domain.ChatWeather;
import kolyat.telegram.repository.ChatWeatherRepository;
import kolyat.telegram.service.WeatherService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.bots.commandbot.commands.BotCommand;

import javax.annotation.PostConstruct;

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

    @PostConstruct
    public void postConstructorSchedule() {
        for (ChatWeather chatWeather : chatWeatherRepository.findAllBySubscribed(true)) {
            weatherService.schedule(this, chatWeather);
        }
    }

    @Override
    public void processNonCommandUpdate(Update update) {

    }
}
