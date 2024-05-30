package ru.omon4412.minibank.model;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class SendMessageEvent extends ApplicationEvent {
    private final Long chatId;
    private final String message;

    public SendMessageEvent(Object source, Long chatId, String message) {
        super(source);
        this.chatId = chatId;
        this.message = message;
    }

}
