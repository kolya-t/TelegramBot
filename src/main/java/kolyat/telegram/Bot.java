package kolyat.telegram;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    public Bot(@Value("${kolyat.telegram-bot.bot-username}") String botUsername, BotCommand[] commands) {
        super(botUsername);
        registerAll(commands);
    }

    @Override
    public void processNonCommandUpdate(Update update) {

    }
}
