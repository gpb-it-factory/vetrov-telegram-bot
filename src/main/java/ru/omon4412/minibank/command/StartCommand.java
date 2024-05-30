package ru.omon4412.minibank.command;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.omon4412.minibank.model.SendMessageEvent;

@Component
@RequiredArgsConstructor
class StartCommand implements Command {
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void execute(Update update) {
        String username = update.getMessage().getFrom().getFirstName();
        String message = String.format("""
                        Привет, %s!
                        Это телеграм бот Mini Bank.

                        Доступные команды:
                        %s
                        %s
                        %s""", username,
                Commands.PING.getCommand(), Commands.REGISTER.getCommand(), Commands.START.getCommand());
        SendMessageEvent sendMessageEvent = new SendMessageEvent(this, update.getMessage().getChatId(), message);
        eventPublisher.publishEvent(sendMessageEvent);
    }

    @Override
    public String getCommand() {
        return Commands.START.getCommand();
    }
}
