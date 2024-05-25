package ru.omon4412.minibank.business.command;

import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.omon4412.minibank.business.service.MessageService;

@RequiredArgsConstructor
class PingCommand implements Command {
    private final MessageService messageService;

    @Override
    public void execute(Update update) {
        messageService.sendMessage(update.getMessage().getChatId(), "pong");
    }
}
