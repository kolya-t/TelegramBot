package kolyat.telegram.command;

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
public class StopCommand extends BotCommand {
    @Autowired
    private ChatWeatherRepository chatWeatherRepository;

    public StopCommand() {
        super("stop", "");
    }

    @Override
    @SneakyThrows(TelegramApiException.class)
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        SendMessage answer = new SendMessage()
                .setChatId(chat.getId())
                .setReplyMarkup(new ReplyKeyboardRemove());

        if (chatWeatherRepository.existsByChatId(chat.getId())) {
            chatWeatherRepository.deleteByChatId(chat.getId());
            answer.setText("Удаляю все данные о вас из базы. Больше не буду присылать вам погоду.");
        } else {
            answer.setText("Вас нет в моей базе данных");
        }

        absSender.execute(answer);
    }
}
