package ru.kolyat.telegram;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

@Component
public class Bot extends TelegramLongPollingBot {

    @Getter
    @Value("${ru.kolyat.telegram.botToken}")
    private String botToken;

    @Getter
    @Value("${ru.kolyat.telegram.botUsername}")
    private String botUsername;

    @Override
    public void onUpdateReceived(Update update) {

    }
}
