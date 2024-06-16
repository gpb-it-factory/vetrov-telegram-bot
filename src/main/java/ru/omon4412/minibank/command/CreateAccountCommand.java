package ru.omon4412.minibank.command;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.omon4412.minibank.dto.NewAccountDto;
import ru.omon4412.minibank.model.ResponseResult;
import ru.omon4412.minibank.model.TelegramMessage;
import ru.omon4412.minibank.service.MiddleServiceGateway;

@Component
@RequiredArgsConstructor
class CreateAccountCommand implements Command {
    private final MiddleServiceGateway middleServiceGateway;

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

        ResponseResult responseResult = middleServiceGateway.createAccount(newAccountDto, userId);
        return new TelegramMessage(update.getMessage().getChatId(), responseResult.getMessage());
    }

    @Override
    public String getCommand() {
        return Commands.CREATEACCOUNT.getCommand();
    }
}
