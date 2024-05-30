package ru.omon4412.minibank.command;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.omon4412.minibank.model.SendMessageEvent;

@Component
@RequiredArgsConstructor
class PingCommand implements Command {
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void execute(Update update) {
        SendMessageEvent pong = new SendMessageEvent(this, update.getMessage().getChatId(), "pong");
        eventPublisher.publishEvent(pong);
    }

    @Override
    public String getCommand() {
        return Commands.PING.getCommand();
    }
}
