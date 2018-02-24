package kolyat.telegram;

import com.github.fedy2.weather.data.Channel;
import kolyat.telegram.domain.ChatWeather;
import kolyat.telegram.repository.ChatWeatherRepository;
import kolyat.telegram.service.WeatherService;
import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.Location;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.exceptions.TelegramApiException;

@Component
public class WeatherBot extends TelegramLongPollingCommandBot {
    @Getter
    @Value("${kolyat.telegram-bot.bot-token}")
    private String botToken;

    @Autowired
    private ChatWeatherRepository chatWeatherRepository;

    @Autowired
    private WeatherService weatherService;

    public WeatherBot(@Value("${kolyat.telegram-bot.bot-username}") String botUsername, BotCommand[] commands) {
        super(botUsername);
        registerAll(commands);
    }

    @Scheduled(cron = "0 0/30 6 * * *")
    public void sendForecastForTodayToAll() {
        for (ChatWeather chatWeather : chatWeatherRepository.findAllBySubscribed(true)) {
            weatherService.sendForecastForToday(this, chatWeather);
        }
    }

    @Override
    public void processNonCommandUpdate(Update update) {
        if (update.hasMessage() && update.getMessage().hasLocation()) {
            processLocation(update.getMessage().getLocation(), update.getMessage().getChat());
        }
    }

    @SneakyThrows(TelegramApiException.class)
    private void processLocation(Location location, Chat chat) {
        SendMessage answer = new SendMessage()
                .setChatId(chat.getId())
                .setReplyMarkup(new ReplyKeyboardRemove());

        Channel channel = weatherService.getForecastChannelForLocation(location);
        ChatWeather chatWeather = chatWeatherRepository.findByChatId(chat.getId());
        if (chatWeather != null) {
            answer.enableMarkdown(true)
                    .setText("Вы уже есть в нашей базе данных (*%s*, *%s*, *%s*). " +
                            "Если хотите изменить данные о вашем местоположении, выполните /start");
        } else if (channel != null) {
            chatWeatherRepository.save(new ChatWeather(chat.getId(), location));
            answer.enableMarkdown(true)
                    .setReplyMarkup(new ReplyKeyboardRemove())
                    .setText(String.format("Буду получать данные о погоде для *%s*, *%s*, *%s*. Теперь можете " +
                                    "разрешить мне присылать вам прогноз погоды в 6:30, выполнив /subscribe",
                            channel.getLocation().getCity(),
                            channel.getLocation().getRegion(),
                            channel.getLocation().getCountry()));
        } else {
            answer.setText("Не могу получить данные о погоде по вашей геопозиции");
        }

        execute(answer);
    }
}
