package ru.omon4412.minibank.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.omon4412.minibank.bot.MiniBankBot;
import ru.omon4412.minibank.model.TelegramMessage;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageServiceImplTest {
    @Mock
    private MiniBankBot miniBankBot;

    @InjectMocks
    private MessageServiceImpl messageService;

    @Test
    void test_sendMessage_success() throws TelegramApiException {
        TelegramMessage event = new TelegramMessage(1L, "Test");
        Message message = new Message();
        when(miniBankBot.execute(any(SendMessage.class))).thenReturn(message);

        messageService.sendMessage(event);

        verify(miniBankBot, times(1)).execute(any(SendMessage.class));
    }

    @Test
    void test_sendMessage_whenMessageIsBlank() throws TelegramApiException {
        TelegramMessage event = new TelegramMessage(1L, "    ");

        messageService.sendMessage(event);

        verify(miniBankBot, times(0)).execute(any(SendMessage.class));
    }
}