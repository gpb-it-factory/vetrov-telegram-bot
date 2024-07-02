package ru.omon4412.minibank.command;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.omon4412.minibank.dto.ResponseAccountDto;
import ru.omon4412.minibank.model.TelegramMessage;
import ru.omon4412.minibank.service.MiddleServiceGateway;
import ru.omon4412.minibank.util.Result;

import java.math.BigDecimal;
import java.util.Collection;

@Component
class CurrentBalanceCommand implements Command {
    private final MiddleServiceGateway middleServiceGateway;
    private final Counter currentBalanceCommandCounter;

    CurrentBalanceCommand(MiddleServiceGateway middleServiceGateway, MeterRegistry meterRegistry) {
        this.middleServiceGateway = middleServiceGateway;
        this.currentBalanceCommandCounter = meterRegistry.counter("commands.currentBalance.executions");
    }

    @Override
    public TelegramMessage execute(Update update) {
        Long userId = update.getMessage().getFrom().getId();
        String username = update.getMessage().getFrom().getUserName();
        if (username == null) {
            return new TelegramMessage(update.getMessage().getChatId(),
                    "Для работы с ботом вам нужен telegram username");
        }

        Result<Collection<ResponseAccountDto>> responseResult = middleServiceGateway.getUserAccounts(userId);
        StringBuilder message = new StringBuilder();
        if (responseResult.isFailure()) {
            message.append(responseResult.exceptionOrNull().getMessage());
        } else {
            Collection<ResponseAccountDto> accounts = responseResult.getOrNull();
            if (accounts.isEmpty()) {
                message.append("Нет активных счетов.");
            } else {
                message.append("Ваши активные счета:\n");
                message.append("--------------------------------------------\n");
                for (ResponseAccountDto account : accounts) {
                    message.append(" - ");
                    message.append(account.getAccountName());
                    message.append(" - ");
                    message.append(account.getAmount());
                    message.append(" рублей\n");
                }
                message.append("--------------------------------------------\n");
                message.append("Сумма по счетам: ");
                message.append(accounts.stream()
                        .map(ResponseAccountDto::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add));
                message.append(" рублей\n");
            }
        }
        currentBalanceCommandCounter.increment();
        return new TelegramMessage(update.getMessage().getChatId(), message.toString());
    }

    @Override
    public String getCommand() {
        return Commands.CURRENTBALANCE.getCommand();
    }
}
