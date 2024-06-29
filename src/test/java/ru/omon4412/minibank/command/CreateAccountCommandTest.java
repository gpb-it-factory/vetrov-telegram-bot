package ru.omon4412.minibank.command;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.omon4412.minibank.dto.NewAccountDto;
import ru.omon4412.minibank.model.TelegramMessage;
import ru.omon4412.minibank.service.MiddleServiceGateway;
import ru.omon4412.minibank.util.Result;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateAccountCommandTest {
    @Mock
    private MiddleServiceGateway middleServiceGateway;

    @InjectMocks
    private CreateAccountCommand createAccountCommand;

    @Test
    void userCreateAccount_failed_ExecuteWithoutUsername() {
        Update update = mockUpdate(null, "/createaccount На отдых", 1L);

        TelegramMessage result = createAccountCommand.execute(update);

        assertEquals("Для работы с ботом вам нужен telegram username", result.message());
    }

    @Test
    void userCreateAccount_success_ExecuteWithAccountName() {
        Update update = mockUpdate("testuser", "/createaccount На отдых", 1L);
        Result<String> responseResult = new Result.Success<>("Счёт создан успешно");
        NewAccountDto newAccountDto = new NewAccountDto();
        newAccountDto.setAccountName("На отдых");
        when(middleServiceGateway.createAccount(newAccountDto, 0L))
                .thenReturn(responseResult);

        TelegramMessage result = createAccountCommand.execute(update);

        assertEquals("Счёт создан успешно", result.message());
    }

    @Test
    void userCreateAccount_success_ExecuteWithoutAccountName() {
        Update update = mockUpdate("testuser", "/createaccount", 1L);
        Result<String> responseResult = new Result.Success<>("Счёт создан успешно");
        NewAccountDto newAccountDto = new NewAccountDto();
        when(middleServiceGateway.createAccount(newAccountDto, 0L))
                .thenReturn(responseResult);

        TelegramMessage result = createAccountCommand.execute(update);

        assertEquals("Счёт создан успешно", result.message());
    }

    private Update mockUpdate(String username, String text, Long chatId) {
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        User user = mock(User.class);

        when(update.getMessage()).thenReturn(message);
        when(message.getFrom()).thenReturn(user);
        if (username != null) {
            when(user.getUserName()).thenReturn(username);
        }
        if (text != null) {
            lenient().when(message.getText()).thenReturn(text);
        }
        if (chatId != null) {
            when(message.getChatId()).thenReturn(chatId);
        }

        return update;
    }
}