package kolyat.telegram.handler;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

public abstract class AbstractHandler extends TelegramLongPollingBot {
    @Getter
    @Value("${kolyat.telegram-bot.bot-token}")
    private String botToken;

    @Getter
    @Value("${kolyat.telegram-bot.bot-username}")
    private String botUsername;

    @Override
    public void onUpdateReceived(Update update) {
    }
}
