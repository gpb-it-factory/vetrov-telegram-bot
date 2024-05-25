package ru.omon4412.minibank.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.omon4412.minibank.business.bot.MiniBankBot;

@Component
@RequiredArgsConstructor
@Slf4j
public class Initializer {
    private final MiniBankBot miniBankBot;

    @EventListener({ContextRefreshedEvent.class})
    public void init() {
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(miniBankBot);
            log.info("Telegram bot запущен");
        } catch (TelegramApiException e) {
            log.error("Ошибка при запуске бота: " + e.getMessage());
            System.exit(1);
        }
    }
}
