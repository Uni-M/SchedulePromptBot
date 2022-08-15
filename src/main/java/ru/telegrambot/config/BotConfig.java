package ru.telegrambot.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.telegrambot.ReminderBot;
import ru.telegrambot.handler.CallbackQueryHandler;
import ru.telegrambot.handler.MessageHandler;
import ru.telegrambot.service.PromptService;
import ru.telegrambot.service.UserService;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class BotConfig {

    private final CallbackQueryHandler callbackQueryHandler;
    private final MessageHandler messageHandler;
    private final UserService userService;
    private final PromptService promptService;

    @Bean
    public void createBot() throws TelegramApiException {

        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);

        ReminderBot bot = new ReminderBot(messageHandler,
                callbackQueryHandler,
                userService,
                promptService);

        try {
            telegramBotsApi.registerBot(bot);
            log.info("Bot registered");
        } catch (TelegramApiRequestException e) {
            log.error("Error register bot. Exception: " + e);
        }
    }

}
