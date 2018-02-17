package ru.kolyat.telegram;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import ru.kolyat.telegram.handler.WeatherHandler;

@EnableScheduling
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
    public CommandLineRunner registerBot(WeatherHandler weatherHandler) {
        return args -> api.registerBot(weatherHandler);
    }
}
