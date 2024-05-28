package ru.omon4412.minibank.business.bot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.omon4412.minibank.business.command.CommandHandler;
import ru.omon4412.minibank.business.service.MessageServiceImpl;
import ru.omon4412.minibank.business.service.MiddleServiceGatewayImpl;
import ru.omon4412.minibank.client.MiddleServiceClient;

@Component
@Slf4j
public class MiniBankBot extends TelegramLongPollingBot {
    private final String botUsername;
    private final CommandHandler commandHandler;

    @Autowired
    public MiniBankBot(@Value("${bot.name}") String botUsername, @Value("${bot.token}") String botToken,
                       MiddleServiceClient middleServiceClient) {
        super(botToken);
        this.botUsername = botUsername;
        this.commandHandler = new CommandHandler(new MessageServiceImpl(this),
                new MiddleServiceGatewayImpl(middleServiceClient));
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            log.info("Получено сообщение '{}' от '{}'", messageText, update.getMessage().getFrom().getId());
            if (messageText.startsWith("/")) {
                String commandName = messageText.split(" ")[0];
                commandHandler.handleCommand(commandName, update);
            } else {
                commandHandler.handleCommand("/default", update);
            }
        }
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }
}
