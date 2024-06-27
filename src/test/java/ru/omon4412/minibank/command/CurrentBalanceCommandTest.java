package ru.omon4412.minibank.command;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.omon4412.minibank.dto.ResponseAccountDto;
import ru.omon4412.minibank.model.TelegramMessage;
import ru.omon4412.minibank.service.MiddleServiceGateway;
import ru.omon4412.minibank.util.Result;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CurrentBalanceCommandTest {
    private final String line = "--------------------------------------------\n";
    @Mock
    private MiddleServiceGateway middleServiceGateway;
    @InjectMocks
    private CurrentBalanceCommand currentBalanceCommand;

    @Test
    public void test_SuccessfulGetUserAccount() {
        Update update = mockUpdate("testuser", "/currentbalance", 1L);
        Collection<ResponseAccountDto> responseAccountDtos = new ArrayList<>();
        ResponseAccountDto responseAccountDto = new ResponseAccountDto();
        responseAccountDto.setAccountName("Test");
        responseAccountDto.setAccountId("TestId");
        responseAccountDto.setAmount(5000L);
        responseAccountDtos.add(responseAccountDto);

        Result<Collection<ResponseAccountDto>> responseResult = new Result.Success<>(responseAccountDtos);

        when(middleServiceGateway.getUserAccounts(anyLong()))
                .thenReturn(responseResult);

        TelegramMessage result = currentBalanceCommand.execute(update);

        String expectedMessage = "Ваши активные счета:\n" +
                line +
                " - Test - 5000.0 рублей\n" +
                line +
                "Сумма по счетам: 5000.0 рублей\n";
        assertEquals(expectedMessage, result.message());
    }

    @Test
    public void test_SuccessfulGet2UserAccounts() {
        Update update = mockUpdate("testuser", "/currentbalance", 1L);
        Collection<ResponseAccountDto> responseAccountDtos = new ArrayList<>();
        ResponseAccountDto responseAccountDto1 = new ResponseAccountDto();
        responseAccountDto1.setAccountName("Test1");
        responseAccountDto1.setAccountId("TestId1");
        responseAccountDto1.setAmount(5000L);
        ResponseAccountDto responseAccountDto2 = new ResponseAccountDto();
        responseAccountDto2.setAccountName("Test2");
        responseAccountDto2.setAccountId("TestId2");
        responseAccountDto2.setAmount(7000L);
        responseAccountDtos.add(responseAccountDto1);
        responseAccountDtos.add(responseAccountDto2);

        Result<Collection<ResponseAccountDto>> responseResult = new Result.Success<>(responseAccountDtos);

        when(middleServiceGateway.getUserAccounts(anyLong()))
                .thenReturn(responseResult);

        TelegramMessage result = currentBalanceCommand.execute(update);

        String expectedMessage = "Ваши активные счета:\n" +
                line +
                " - Test1 - 5000.0 рублей\n" +
                " - Test2 - 7000.0 рублей\n" +
                line +
                "Сумма по счетам: 12000.0 рублей\n";
        assertEquals(expectedMessage, result.message());
    }

    @Test
    void test_SuccessfulWhenZeroUserAccounts() {
        Update update = mockUpdate("testuser", "/currentbalance", 1L);
        Result<Collection<ResponseAccountDto>> responseResult = new Result.Success<>(Collections.emptyList());

        when(middleServiceGateway.getUserAccounts(anyLong()))
                .thenReturn(responseResult);
        TelegramMessage result = currentBalanceCommand.execute(update);

        assertEquals("Нет активных счетов.", result.message());
    }

    @Test
    void test_ExecuteWithoutUsername() {
        Update update = mockUpdate(null, "/currentbalance", 1L);

        TelegramMessage result = currentBalanceCommand.execute(update);

        assertEquals("Для работы с ботом вам нужен telegram username", result.message());
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