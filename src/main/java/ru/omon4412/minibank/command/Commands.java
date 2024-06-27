package ru.omon4412.minibank.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum Commands {
    START("/start", true),
    PING("/ping", true),
    REGISTER("/register", true),
    CREATEACCOUNT("/createaccount", true),
    CURRENTBALANCE("/currentbalance", true),
    TRANSFER("/transfer", true),
    // Служебные команды
    DEFAULT("/default", false),
    UNKNOWN("/unknown", false);

    private final String command;
    private final boolean isMainCommand;

    public static List<Commands> getCommands() {
        return Arrays.stream(values())
                .filter(Commands::isMainCommand)
                .collect(Collectors.toList());
    }
}
