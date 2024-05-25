package ru.omon4412.minibank.business.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.omon4412.minibank.business.bot.MiniBankBot;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageServiceImpl implements MessageService {
    private final MiniBankBot miniBankBot;

    @Override
    public void sendMessage(Long chatId, String message) {
        if (message.isBlank()) {
            log.warn("Получено пустое сообщение");
            return;
        }

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId.toString());
        sendMessage.setText(message);

        try {
            miniBankBot.execute(sendMessage);
            log.info("Сообщение '{}' отправлено в чат '{}'", message, chatId);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }
}
