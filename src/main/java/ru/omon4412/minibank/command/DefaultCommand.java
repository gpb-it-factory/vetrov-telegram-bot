package ru.omon4412.minibank.command;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.omon4412.minibank.model.SendMessageEvent;

@Component
@RequiredArgsConstructor
public class DefaultCommand implements Command {
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void execute(Update update) {
        SendMessageEvent sendMessageEvent = new SendMessageEvent(this,
                update.getMessage().getChatId(), "Команда должна начинаться с /");
        eventPublisher.publishEvent(sendMessageEvent);
    }

    @Override
    public String getCommand() {
        return Commands.DEFAULT.getCommand();
    }
}
