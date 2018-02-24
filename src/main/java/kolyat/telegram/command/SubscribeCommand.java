package kolyat.telegram.command;

import kolyat.telegram.domain.ChatWeather;
import kolyat.telegram.repository.ChatWeatherRepository;
import kolyat.telegram.service.WeatherService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.exceptions.TelegramApiException;

@Slf4j
@Component
public class SubscribeCommand extends BotCommand {
    @Autowired
    private ChatWeatherRepository chatWeatherRepository;

    @Autowired
    private WeatherService weatherService;

    public SubscribeCommand() {
        super("subscribe", "");
    }

    @Override
    @SneakyThrows(TelegramApiException.class)
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        ChatWeather chatWeather = chatWeatherRepository.findByChatId(chat.getId());
        if (chatWeather != null && !chatWeather.getSubscribed()) {
            weatherService.schedule(absSender, chatWeather);
            chatWeather.setSubscribed(true);
            chatWeatherRepository.save(chatWeather);
            absSender.execute(new SendMessage()
                    .setChatId(chat.getId())
                    .setText("Я буду присылать вам прогноз погоды каждый день в 6:30")
                    .setReplyMarkup(new ReplyKeyboardRemove()));
        }
    }
}
