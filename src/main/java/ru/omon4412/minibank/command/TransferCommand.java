package ru.omon4412.minibank.command;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.omon4412.minibank.dto.CreateTransferRequestDto;
import ru.omon4412.minibank.dto.TransferResponseDto;
import ru.omon4412.minibank.dto.UserIdResponseDto;
import ru.omon4412.minibank.model.TelegramMessage;
import ru.omon4412.minibank.service.MessageService;
import ru.omon4412.minibank.service.MiddleServiceGateway;
import ru.omon4412.minibank.util.Result;

@Component
@RequiredArgsConstructor
class TransferCommand implements Command {
    private final MessageService messageService;
    private final MiddleServiceGateway middleServiceGateway;

    @Override
    public TelegramMessage execute(Update update) {
        String username = update.getMessage().getFrom().getUserName();
        if (username == null) {
            return new TelegramMessage(update.getMessage().getChatId(),
                    "Для работы с ботом вам нужен telegram username");
        }

        String transferCommand = Commands.TRANSFER.getCommand();
        String messageText = update.getMessage().getText();

        TelegramMessage notValidTelegramMessage = new TelegramMessage(update.getMessage().getChatId(),
                "Неправильный формат команды. Используйте: "
                        + transferCommand + " [toTelegramUser] [amount]");

        if (messageText == null || !messageText.startsWith(transferCommand + " ")) {
            return notValidTelegramMessage;
        }

        String[] parts = messageText.split(" ", 3);
        if (parts.length < 3) {
            return notValidTelegramMessage;
        }
        String toTelegramUser = parts[1].trim();
        String amount = parts[2].trim();
        double amountDouble;
        try {
            amountDouble = Double.parseDouble(amount);
        } catch (NumberFormatException e) {
            return notValidTelegramMessage;
        }

        if (amountDouble <= 0) {
            return new TelegramMessage(update.getMessage().getChatId(),
                    "Сумма перевода должна быть больше нуля");
        }
        if (toTelegramUser.equals(username)) {
            return new TelegramMessage(update.getMessage().getChatId(),
                    "Нельзя перевести средства самому себе");
        }

        CreateTransferRequestDto createTransferRequestDto = new CreateTransferRequestDto();
        createTransferRequestDto.setFrom(username);
        createTransferRequestDto.setTo(toTelegramUser);
        createTransferRequestDto.setAmount(amountDouble);
        Result<UserIdResponseDto> toTelegramUserId = middleServiceGateway.getUserIdByUserName(toTelegramUser);
        Result<TransferResponseDto> transfer = middleServiceGateway.transfer(createTransferRequestDto);
        String message;

        if (toTelegramUserId.isSuccess()) {
            if (transfer.isSuccess()) {
                message = "Перевод успешно совершен";
                messageService.sendMessage(new TelegramMessage(toTelegramUserId.getOrNull().getUserId(),
                        "Зачислен перевод: " + amount + " руб. от " + username));
            } else {
                message = transfer.exceptionOrNull().getMessage();
            }
        } else {
            message = transfer.exceptionOrNull().getMessage();
        }

        return new TelegramMessage(update.getMessage().getChatId(), message);
    }

    @Override
    public String getCommand() {
        return Commands.TRANSFER.getCommand();
    }
}
