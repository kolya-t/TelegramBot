package kolyat.telegram.command;

import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.Collections;

@Component
public class StartCommand extends BotCommand {
    public StartCommand() {
        super("start", "");
    }

    @Override
    @SneakyThrows(TelegramApiException.class)
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        SendMessage answer = new SendMessage()
                .setChatId(chat.getId())
                .setText("Я буду узнавать для вас прогноз погоды. " +
                        "Отправьте мне вашу геопозицию, чтобы я мог начать.");

        if (chat.isUserChat()) {
            KeyboardButton button = new KeyboardButton()
                    .setText("\uD83C\uDF0E Отправить геопозицию")
                    .setRequestLocation(true);

            KeyboardRow row = new KeyboardRow();
            row.add(button);

            ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup()
                    .setKeyboard(Collections.singletonList(row))
                    .setResizeKeyboard(true)
                    .setOneTimeKeyboard(true);

            answer.setReplyMarkup(keyboardMarkup);
        }

        absSender.execute(answer);
    }
}
