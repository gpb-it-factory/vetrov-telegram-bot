package ru.omon4412.minibank.business.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.omon4412.minibank.business.dto.UserResponseDto;
import ru.omon4412.minibank.business.model.RegistrationResult;
import ru.omon4412.minibank.business.service.MessageService;
import ru.omon4412.minibank.business.service.MiddleServiceGateway;

@RequiredArgsConstructor
@Slf4j
public class RegisterCommand implements Command {
    private final MessageService messageService;
    private final MiddleServiceGateway middleServiceGateway;

    @Override
    public void execute(Update update) {
        Long userId = update.getMessage().getFrom().getId();
        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setUserId(userId);
        log.info("Пользователь {} пытается зарегистрироваться", userId);
        RegistrationResult registrationResult = middleServiceGateway.registerUser(userResponseDto);
        log.info("Регистрация пользователя {} завершена. Статус: {}. Сообщение: {}",
                userId, registrationResult.isSuccess(), registrationResult.getMessage());
        messageService.sendMessage(update.getMessage().getChatId(), registrationResult.getMessage());
    }
}
