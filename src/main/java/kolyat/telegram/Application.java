package kolyat.telegram;

import kolyat.telegram.handler.AbstractHandler;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;

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
    public TaskScheduler taskScheduler() {
        return new ThreadPoolTaskScheduler();
    }

    @Bean
    public CommandLineRunner registerBot(AbstractHandler[] handlers) {
        return args -> {
            for (AbstractHandler handler : handlers) {
                api.registerBot(handler);
            }
        };
    }
}
