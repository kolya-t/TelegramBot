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
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.exceptions.TelegramApiException;

@Component
public class UnsubscribeCommand extends BotCommand {
    @Autowired
    private ChatWeatherRepository chatWeatherRepository;

    public UnsubscribeCommand() {
        super("unsubscribe", "");
    }

    @Override
    @SneakyThrows(TelegramApiException.class)
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        SendMessage answer = new SendMessage()
                .setChatId(chat.getId())
                .setReplyMarkup(new ReplyKeyboardRemove());

        ChatWeather chatWeather = chatWeatherRepository.findByChatId(chat.getId());
        if (chatWeather == null) {
            answer.setText("Вас нет в моей базе данных. Начните сначала (/start)");
        } else if (chatWeather.getSubscribed()) {
            chatWeather.setSubscribed(false);
            chatWeatherRepository.save(chatWeather);
            answer.setReplyMarkup(WeatherBot.createKeyboardMarkup(true, false))
                    .setText("Вы отписались от рассылки прогноза погоды в 6:30");
        } else {
            answer.setReplyMarkup(WeatherBot.createKeyboardMarkup(true, false))
                    .setText("Вы не подписаны на ежедневный прогноз погоды");
        }

        absSender.execute(answer);
    }
}
