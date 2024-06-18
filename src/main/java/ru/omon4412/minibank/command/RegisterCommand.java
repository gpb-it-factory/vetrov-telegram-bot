package ru.omon4412.minibank.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.omon4412.minibank.dto.UserRequestDto;
import ru.omon4412.minibank.model.TelegramMessage;
import ru.omon4412.minibank.service.MiddleServiceGateway;
import ru.omon4412.minibank.util.Result;

@Component
@RequiredArgsConstructor
@Slf4j
public class RegisterCommand implements Command {
    private final MiddleServiceGateway middleServiceGateway;

    @Override
    public TelegramMessage execute(Update update) {
        Long userId = update.getMessage().getFrom().getId();
        String username = update.getMessage().getFrom().getUserName();
        if (username == null) {
            return new TelegramMessage(update.getMessage().getChatId(),
                    "Для работы с ботом вам нужен telegram username");
        }
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setUserId(userId);
        userRequestDto.setUserName(username);
        log.info("Пользователь {} пытается зарегистрироваться", userId);
        Result<String> responseResult = middleServiceGateway.registerUser(userRequestDto);
        String message;
        if (responseResult.isFailure()) {
            message = responseResult.exceptionOrNull().getMessage();
        } else {
            message = responseResult.getOrNull();
        }
        log.info("Регистрация пользователя {} завершена. Статус: {}. Сообщение: {}",
                userId, responseResult.isSuccess(), message);
        return new TelegramMessage(update.getMessage().getChatId(), message);
    }

    @Override
    public String getCommand() {
        return Commands.REGISTER.getCommand();
    }
}
