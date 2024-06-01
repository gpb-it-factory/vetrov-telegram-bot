package ru.omon4412.minibank.command;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.omon4412.minibank.model.TelegramMessage;
import ru.omon4412.minibank.service.MessageService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CommandHandler {
    private final Map<String, Command> commandNameToCommand = new HashMap<>();
    private final UnknownCommand unknownCommand;
    private final MessageService messageService;

    @Autowired
    public CommandHandler(List<Command> commands, UnknownCommand unknownCommand, MessageService messageService) {
        this.unknownCommand = unknownCommand;
        this.messageService = messageService;
        for (Command command : commands) {
            String commandName = command.getCommand();
            if (commandNameToCommand.containsKey(commandName)) {
                throw new IllegalArgumentException("Обнаружен дублирующий обработчик команд для команды:" + commandName);
            }
            commandNameToCommand.put(commandName, command);
        }
    }


    public void handleCommand(String commandName, Update update) {
        Command command = commandNameToCommand.getOrDefault(commandName, unknownCommand);
        TelegramMessage execute = command.execute(update);
        messageService.sendMessage(execute);
    }
}
