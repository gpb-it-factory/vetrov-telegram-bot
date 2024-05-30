package ru.omon4412.minibank.command;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CommandHandler {
    private final Map<String, Command> commandNameToCommand = new HashMap<>();
    private final UnknownCommand unknownCommand;

    @Autowired
    public CommandHandler(List<Command> commands, UnknownCommand unknownCommand) {
        this.unknownCommand = unknownCommand;
        for (Command command : commands) {
            commandNameToCommand.put(command.getCommand(), command);
        }
    }


    public void handleCommand(String commandName, Update update) {
        Command command = commandNameToCommand.getOrDefault(commandName, unknownCommand);
        command.execute(update);
    }
}
