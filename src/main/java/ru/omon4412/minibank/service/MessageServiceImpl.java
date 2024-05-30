package ru.omon4412.minibank.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.omon4412.minibank.bot.MiniBankBot;
import ru.omon4412.minibank.model.SendMessageEvent;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageServiceImpl implements MessageService {
    private final MiniBankBot miniBankBot;

    /**
     * Я так сделал, потому что когда пытаюсь отдать спрингу регистрацию команд,
     * потому что когда
     * public CommandHandler(List<Command> commands) {
     * for (Command command : commands) {
     * commandNameToCommand.put(command.getCommand(), command);
     * }
     * }
     * то возникает циклическая зависимость
     * ┌─────┐
     * |  miniBankBot defined in file [MiniBankBot.class]
     * ↑     ↓
     * |  commandHandler defined in file [CommandHandler.class]
     * ↑     ↓
     * |  defaultCommand defined in file [DefaultCommand.class]
     * ↑     ↓
     * |  messageServiceImpl defined in file [MessageServiceImpl.class]
     * └─────┘
     * И мне надо поменять реализацию команд, чтобы они не отправляли сообщение в MessageService, а только возвращали String, и в самом боте его отправлять пользователю,
     * но я решил сделать через EventListener
     * Не знаю насколько это разумное решение
     *
     * @param event
     */
    @EventListener
    public void handleSendMessageEvent(SendMessageEvent event) {
        Long chatId = event.getChatId();
        String message = event.getMessage();

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

    @Override
    public void sendMessage(Long chatId, String message) {
        handleSendMessageEvent(new SendMessageEvent(this, chatId, message));
    }
}
