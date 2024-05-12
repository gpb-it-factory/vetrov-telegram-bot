package ru.omon4412.minibank.command;

import org.telegram.telegrambots.meta.api.objects.Update;
import ru.omon4412.minibank.service.MessageService;

import java.util.HashMap;
import java.util.Map;

public class CommandHandler {
    private final Map<String, Command> commands = new HashMap<>();
    private final Command unknownCommand;

    public CommandHandler(MessageService messageService) {
        registerCommand("/start", new StartCommand(messageService));
        registerCommand("/ping", new PingCommand(messageService));
        registerCommand("/default", new DefaultCommand(messageService));
        unknownCommand = new UnknownCommand(messageService);
    }

    public void registerCommand(String commandName, Command command) {
        commands.put(commandName, command);
    }

    public void handleCommand(String commandName, Update update) {
        Command command = commands.getOrDefault(commandName, unknownCommand);
        command.execute(update);
    }
}
