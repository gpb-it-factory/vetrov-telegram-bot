package ru.omon4412.minibank.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum Commands {
    START("/start", true),
    PING("/ping", true),
    REGISTER("/register", true),
    // Служебные команды
    DEFAULT("/default", false),
    UNKNOWN("/unknown", false);

    private final String command;
    private final boolean isMainCommand;

    public static long countOfCommands() {
        return Arrays.stream(values())
                .filter(Commands::isMainCommand)
                .count();
    }
}
