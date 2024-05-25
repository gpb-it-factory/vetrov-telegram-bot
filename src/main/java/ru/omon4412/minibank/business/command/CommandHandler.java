package ru.omon4412.minibank.business.command;

import org.telegram.telegrambots.meta.api.objects.Update;
import ru.omon4412.minibank.business.service.MessageService;
import ru.omon4412.minibank.business.service.MiddleServiceGateway;

import java.util.HashMap;
import java.util.Map;

public class CommandHandler {
    private final Map<String, Command> commands = new HashMap<>();
    private final Command unknownCommand;


    public CommandHandler(MessageService messageService, MiddleServiceGateway middleServiceGateway) {
        registerCommand("/start", new StartCommand(messageService));
        registerCommand("/ping", new PingCommand(messageService));
        registerCommand("/register", new RegisterCommand(messageService, middleServiceGateway));
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
