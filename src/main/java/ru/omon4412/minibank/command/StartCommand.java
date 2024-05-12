package ru.omon4412.minibank.command;

import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.omon4412.minibank.service.MessageService;

@RequiredArgsConstructor
class StartCommand implements Command {
    private final MessageService messageService;

    @Override
    public void execute(Update update) {
        String username = update.getMessage().getFrom().getFirstName();
        String message = String.format("""
                Привет %s!
                Это телеграм бот Mini Bank.

                Доступные команды:
                /start, /ping""", username);
        messageService.sendMessage(update.getMessage().getChatId(), message);
    }
}
