package ru.omon4412.minibank.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.omon4412.minibank.dto.UserRequestDto;
import ru.omon4412.minibank.model.RegistrationResult;
import ru.omon4412.minibank.model.SendMessageEvent;
import ru.omon4412.minibank.service.MiddleServiceGateway;

@Component
@RequiredArgsConstructor
@Slf4j
public class RegisterCommand implements Command {
    private final ApplicationEventPublisher eventPublisher;
    private final MiddleServiceGateway middleServiceGateway;

    @Override
    public void execute(Update update) {
        Long userId = update.getMessage().getFrom().getId();
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setUserId(userId);
        log.info("Пользователь {} пытается зарегистрироваться", userId);
        RegistrationResult registrationResult = middleServiceGateway.registerUser(userRequestDto);
        log.info("Регистрация пользователя {} завершена. Статус: {}. Сообщение: {}",
                userId, registrationResult.isSuccess(), registrationResult.getMessage());
        SendMessageEvent sendMessageEvent = new SendMessageEvent(this,
                update.getMessage().getChatId(), registrationResult.getMessage());
        eventPublisher.publishEvent(sendMessageEvent);
    }

    @Override
    public String getCommand() {
        return Commands.REGISTER.getCommand();
    }
}
