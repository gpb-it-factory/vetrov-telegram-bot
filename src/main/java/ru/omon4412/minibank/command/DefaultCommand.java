package ru.omon4412.minibank.command;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.omon4412.minibank.model.TelegramMessage;

@Component
@RequiredArgsConstructor
public class DefaultCommand implements Command {

    @Override
    public TelegramMessage execute(Update update) {
        return new TelegramMessage(update.getMessage().getChatId(), "Команда должна начинаться с /");
    }

    @Override
    public String getCommand() {
        return Commands.DEFAULT.getCommand();
    }
}
