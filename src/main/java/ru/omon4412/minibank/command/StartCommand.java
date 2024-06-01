package ru.omon4412.minibank.command;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.omon4412.minibank.model.TelegramMessage;

import java.util.Arrays;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
class StartCommand implements Command {

    @Override
    public TelegramMessage execute(Update update) {
        String username = update.getMessage().getFrom().getFirstName();
        String commands = Arrays.stream(Commands.values())
                .limit(Commands.countOfCommands())
                .map(Commands::getCommand)
                .collect(Collectors.joining("\n"));

        String message = String.format(
                "Привет, %s!\nЭто телеграм бот Mini Bank.\n\nДоступные команды:\n%s",
                username, commands
        );

        return new TelegramMessage(update.getMessage().getChatId(), message);
    }

    @Override
    public String getCommand() {
        return Commands.START.getCommand();
    }
}
