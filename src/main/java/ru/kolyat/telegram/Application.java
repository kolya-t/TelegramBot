package ru.kolyat.telegram;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.generics.LongPollingBot;

@SpringBootApplication
public class Application {
    private static final TelegramBotsApi api;

    static {
        ApiContextInitializer.init();
        api = new TelegramBotsApi();
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }

    @Bean
    public CommandLineRunner registerBot(LongPollingBot bot) {
        return args -> api.registerBot(bot);
    }
}
