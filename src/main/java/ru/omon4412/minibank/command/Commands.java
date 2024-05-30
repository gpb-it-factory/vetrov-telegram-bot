package ru.omon4412.minibank.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Commands {
    START("/start"),
    PING("/ping"),
    REGISTER("/register"),
    DEFAULT("/default"),
    UNKNOWN("/unknown");

    private final String command;
}
