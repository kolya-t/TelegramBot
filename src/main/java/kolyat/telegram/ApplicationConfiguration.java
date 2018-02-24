package kolyat.telegram;

import com.github.fedy2.weather.YahooWeatherService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;

import javax.xml.bind.JAXBException;

@Configuration
@EnableScheduling
public class ApplicationConfiguration {
    private static final TelegramBotsApi api;

    static {
        ApiContextInitializer.init();
        api = new TelegramBotsApi();
    }

    @Bean
    public CommandLineRunner registerBot(Bot bot) {
        return args -> {
            api.registerBot(bot);
        };
    }

    @Bean
    public TaskScheduler taskScheduler() {
        return new ThreadPoolTaskScheduler();
    }

    @Bean
    public YahooWeatherService yahooWeatherService() throws JAXBException {
        return new YahooWeatherService();
    }
}
