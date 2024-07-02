package ru.omon4412.minibank.command;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.omon4412.minibank.dto.NewAccountDto;
import ru.omon4412.minibank.model.TelegramMessage;
import ru.omon4412.minibank.service.MiddleServiceGateway;
import ru.omon4412.minibank.util.Result;

@Component
class CreateAccountCommand implements Command {
    private final MiddleServiceGateway middleServiceGateway;
    private final Counter createAccountCommandCounter;

    public CreateAccountCommand(MiddleServiceGateway middleServiceGateway, MeterRegistry meterRegistry) {
        this.middleServiceGateway = middleServiceGateway;
        this.createAccountCommandCounter = meterRegistry.counter("commands.createAccount.executions");
    }

    @Override
    public TelegramMessage execute(Update update) {
        Long userId = update.getMessage().getFrom().getId();
        String username = update.getMessage().getFrom().getUserName();
        if (username == null) {
            return new TelegramMessage(update.getMessage().getChatId(),
                    "Для работы с ботом вам нужен telegram username");
        }

        String messageText = update.getMessage().getText();
        String createAccountCommand = Commands.CREATEACCOUNT.getCommand();

        if (messageText == null || !messageText.startsWith(createAccountCommand)) {
            return new TelegramMessage(update.getMessage().getChatId(),
                    "Неправильный формат команды. Используйте: "
                            + createAccountCommand + " или " + createAccountCommand + " [название]");
        }

        String[] parts = messageText.split(" ", 2);
        String accountName = parts.length > 1 ? parts[1].trim() : null;

        NewAccountDto newAccountDto = new NewAccountDto();
        if (accountName != null && !accountName.isEmpty()) {
            newAccountDto.setAccountName(accountName);
        }

        Result<String> createAccountResult = middleServiceGateway.createAccount(newAccountDto, userId);
        String message;
        if (createAccountResult.isFailure()) {
            message = createAccountResult.exceptionOrNull().getMessage();
        } else {
            message = createAccountResult.getOrNull();
        }
        createAccountCommandCounter.increment();
        return new TelegramMessage(update.getMessage().getChatId(), message);
    }

    @Override
    public String getCommand() {
        return Commands.CREATEACCOUNT.getCommand();
    }
}
