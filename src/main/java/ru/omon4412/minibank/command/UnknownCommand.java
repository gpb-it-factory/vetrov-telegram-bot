package ru.omon4412.minibank.command;

import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.omon4412.minibank.service.MessageService;

@RequiredArgsConstructor
public class UnknownCommand implements Command {
    private final MessageService messageService;

    @Override
    public void execute(Update update) {
        messageService.sendMessage(update.getMessage().getChatId(), "Неизвестная команда");
    }
}
