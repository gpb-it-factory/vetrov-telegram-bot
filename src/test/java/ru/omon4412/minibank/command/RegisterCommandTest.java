package ru.omon4412.minibank.command;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.omon4412.minibank.dto.UserRequestDto;
import ru.omon4412.minibank.model.TelegramMessage;
import ru.omon4412.minibank.service.MiddleServiceGateway;
import ru.omon4412.minibank.util.Result;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterCommandTest {
    @Mock
    private MiddleServiceGateway middleServiceGateway;

    @InjectMocks
    private RegisterCommand registerCommand;

    private static void getValidUserRequestDto() {
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setUserId(1L);
        userRequestDto.setUserName("testuser");
    }

    @Test
    void executeWithValidUsername() {
        Update update = mockUpdate("testuser", 1L);
        getValidUserRequestDto();
        Result<String> responseResult = new Result.Success<>("Вы зарегистрированы!");

        when(middleServiceGateway.registerUser(any(UserRequestDto.class))).thenReturn(responseResult);

        TelegramMessage result = registerCommand.execute(update);

        assertEquals("Вы зарегистрированы!", result.message());
        verify(middleServiceGateway, times(1)).registerUser(any(UserRequestDto.class));
    }

    @Test
    void executeWithoutUsername() {
        Update update = mockUpdate(null, 1L);

        TelegramMessage result = registerCommand.execute(update);

        assertEquals("Для работы с ботом вам нужен telegram username", result.message());
        verify(middleServiceGateway, never()).registerUser(any(UserRequestDto.class));
    }

    private Update mockUpdate(String username, Long userId) {
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        User user = mock(User.class);

        when(update.getMessage()).thenReturn(message);
        when(message.getFrom()).thenReturn(user);

        if (username != null) {
            when(user.getUserName()).thenReturn(username);
        }

        if (userId != null) {
            when(user.getId()).thenReturn(userId);
        }
        when(message.getChatId()).thenReturn(userId);

        return update;
    }
}