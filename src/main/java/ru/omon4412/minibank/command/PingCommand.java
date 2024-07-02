package ru.omon4412.minibank.command;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.omon4412.minibank.model.TelegramMessage;

@Component
class PingCommand implements Command {
    private final Counter pingCommandCounter;

    @Autowired
    public PingCommand(MeterRegistry meterRegistry) {
        this.pingCommandCounter = meterRegistry.counter("commands.ping.executions");
    }

    @Override
    public TelegramMessage execute(Update update) {
        pingCommandCounter.increment();
        return new TelegramMessage(update.getMessage().getChatId(), "pong");
    }

    @Override
    public String getCommand() {
        return Commands.PING.getCommand();
    }
}
