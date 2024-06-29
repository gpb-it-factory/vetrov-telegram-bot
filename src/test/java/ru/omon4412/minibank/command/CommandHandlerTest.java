package ru.omon4412.minibank.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.omon4412.minibank.model.TelegramMessage;
import ru.omon4412.minibank.service.MessageService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommandHandlerTest {

    @Mock
    private MessageService messageService;

    @Mock
    private UnknownCommand unknownCommand;

    @Mock
    private Command knownCommand;

    private CommandHandler commandHandler;

    @BeforeEach
    void setUp() {
        when(knownCommand.getCommand()).thenReturn("/doanything");
        commandHandler = new CommandHandler(List.of(knownCommand), unknownCommand, messageService);
    }

    @Test
    void HandleKnownCommand() {
        Update update = new Update();
        TelegramMessage telegramMessage = new TelegramMessage(1L, "/doanything");

        when(knownCommand.execute(update)).thenReturn(telegramMessage);

        commandHandler.handleCommand("/doanything", update);

        verify(knownCommand, times(1)).execute(update);
        verify(messageService, times(1)).sendMessage(telegramMessage);
    }

    @Test
    void HandleUnknownCommand() {
        Update update = new Update();
        TelegramMessage telegramMessage = new TelegramMessage(1L, "/testest");

        when(unknownCommand.execute(update)).thenReturn(telegramMessage);

        commandHandler.handleCommand("/unknown", update);

        verify(unknownCommand, times(1)).execute(update);
        verify(messageService, times(1)).sendMessage(telegramMessage);
    }

    @Test
    void DuplicateCommandHandlerInitializationFailed() {
        Command duplicateCommand = mock(Command.class);
        when(duplicateCommand.getCommand()).thenReturn("/doanything");

        assertThrows(IllegalArgumentException.class, () -> new CommandHandler(List.of(knownCommand, duplicateCommand), unknownCommand, messageService));
    }
}
