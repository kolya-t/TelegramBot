package kolyat.telegram.command;

import kolyat.telegram.WeatherBot;
import kolyat.telegram.domain.ChatWeather;
import kolyat.telegram.repository.ChatWeatherRepository;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.exceptions.TelegramApiException;

@Component
public class StartCommand extends BotCommand {
    @Autowired
    private ChatWeatherRepository chatWeatherRepository;

    public StartCommand() {
        super("start", "");
    }

    @Override
    @SneakyThrows(TelegramApiException.class)
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        ChatWeather chatWeather = chatWeatherRepository.findByChatId(chat.getId());
        if (chatWeather != null) {
            chatWeatherRepository.delete(chatWeather);
        }
        absSender.execute(new SendMessage()
                .setChatId(chat.getId())
                .setReplyMarkup(WeatherBot.createKeyboardMarkup(false, chat.isUserChat()))
                .setText("Я буду узнавать для вас прогноз погоды. " +
                        "Отправьте мне вашу геопозицию, чтобы я мог начать."));
    }
}
