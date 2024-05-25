package ru.omon4412.minibank.business.command;

import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.omon4412.minibank.business.dto.UserResponseDto;
import ru.omon4412.minibank.business.service.MessageService;
import ru.omon4412.minibank.business.service.MiddleServiceGateway;

@RequiredArgsConstructor
public class RegisterCommand implements Command {
    private final MessageService messageService;
    private final MiddleServiceGateway middleServiceGateway;

    @Override
    public void execute(Update update) {
        Long userId = update.getMessage().getFrom().getId();
        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setUserId(userId);
        middleServiceGateway.registerUser(userResponseDto).ifPresentOrElse(
                success -> {
                    if (success) {
                        messageService.sendMessage(update.getMessage().getChatId(), "Вы зарегистрированы!");
                    } else {
                        messageService.sendMessage(update.getMessage().getChatId(),
                                "Вы уже зарегистрированы.");
                    }
                },
                () -> messageService.sendMessage(update.getMessage().getChatId(),
                        "Сервис недоступен. Пожалуйста, попробуйте позже.")
        );
    }
}
