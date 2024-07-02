package ru.omon4412.minibank.command;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.omon4412.minibank.dto.UserRequestDto;
import ru.omon4412.minibank.model.TelegramMessage;
import ru.omon4412.minibank.service.MiddleServiceGateway;
import ru.omon4412.minibank.util.Result;

@Component
@Slf4j
public class RegisterCommand implements Command {
    private final MiddleServiceGateway middleServiceGateway;
    private final Counter registerCommandCounter;
    private final Counter registerCommandErrorCounter;

    @Autowired
    public RegisterCommand(MiddleServiceGateway middleServiceGateway, MeterRegistry meterRegistry) {
        this.middleServiceGateway = middleServiceGateway;
        this.registerCommandCounter = meterRegistry.counter("commands.register.executions");
        this.registerCommandErrorCounter = meterRegistry.counter("commands.register.errors");
    }

    @Override
    public TelegramMessage execute(Update update) {
        Long userId = update.getMessage().getFrom().getId();
        String username = update.getMessage().getFrom().getUserName();
        if (username == null) {
            registerCommandErrorCounter.increment();
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
            registerCommandErrorCounter.increment();
        } else {
            message = responseResult.getOrNull();
        }
        log.info("Регистрация пользователя {} завершена. Статус: {}. Сообщение: {}",
                userId, responseResult.isSuccess(), message);
        registerCommandCounter.increment();
        return new TelegramMessage(update.getMessage().getChatId(), message);
    }

    @Override
    public String getCommand() {
        return Commands.REGISTER.getCommand();
    }
}
