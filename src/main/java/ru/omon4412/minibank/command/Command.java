package ru.omon4412.minibank.command;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface Command {
    void execute(Update update);

    String getCommand();
}
