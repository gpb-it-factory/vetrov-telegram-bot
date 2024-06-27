package ru.omon4412.minibank.service;

import ru.omon4412.minibank.model.TelegramMessage;

public interface MessageService {

    void sendMessage(TelegramMessage telegramMessage);
}
