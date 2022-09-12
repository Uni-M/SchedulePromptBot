package ru.telegrambot;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.DataBinder;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.telegrambot.handler.CallbackQueryHandler;
import ru.telegrambot.handler.MessageHandler;
import ru.telegrambot.service.PromptService;
import ru.telegrambot.service.UserService;
import ru.telegrambot.utils.EnvHelper;
import ru.telegrambot.validation.AdminValidator;

import javax.inject.Singleton;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Singleton
public class ReminderBot extends TelegramLongPollingBot {

    private final String botUsername = EnvHelper.getValue("BOT_NAME");
    private final String botToken = EnvHelper.getValue("BOT_TOKEN");

    private final CallbackQueryHandler callbackQueryHandler;
    private final MessageHandler messageHandler;
    private final UserService userService;
    private final PromptService promptService;

    private static final ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(1);

    public ReminderBot(MessageHandler messageHandler,
                       CallbackQueryHandler callbackQueryHandler,
                       UserService userService,
                       PromptService promptService) {
        this.messageHandler = messageHandler;
        this.callbackQueryHandler = callbackQueryHandler;
        this.userService = userService;
        this.promptService = promptService;

    }

    {
        scheduler.scheduleAtFixedRate(() -> {

            List<List<String>> actualPrompts = findActualPrompts();

            if (!actualPrompts.isEmpty()) {
                actualPrompts.forEach(info -> {
                    try {
                        execute(scheduledSendMessage(info));
                    } catch (TelegramApiException e) {
                        log.error("Fail to execute scheduled message. Exception: ", e);
                    }
                });
            }
        }, 0, 10, TimeUnit.SECONDS); // TODO сделать корректное время
    }

    @Override
    public void onUpdateReceived(Update update) {
        log.info("New update received");

        try {
            handleUpdate(update);
        } catch (TelegramApiException e) {
            log.error("Fail to execute message. Exception: ", e);
        }

    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    private void handleUpdate(Update update) throws TelegramApiException {

        if (update.hasMessage()) {

            execute(messageHandler.answerMessage(update));

        } else if (update.hasCallbackQuery()) {

            CallbackQuery callbackQuery = update.getCallbackQuery();
            execute(callbackQueryHandler.processCallbackQuery(callbackQuery));

        }

    }

    private SendMessage scheduledSendMessage(List<String> messageInfo) {

        SendMessage sendMessage = new SendMessage(messageInfo.get(0), createMessage(messageInfo));
        sendMessage.enableMarkdown(true);

        return sendMessage;

    }

    @SneakyThrows
    private String createMessage(List<String> strings) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date d = sdf.parse(strings.get(1));

        SimpleDateFormat properFormat = new SimpleDateFormat("dd-MM-yyyy  HH:mm");
        String date = properFormat.format(d);

        return "Дата:* " + date + "*" +
                "\nЗадача:* " + strings.get(2) + "*";
    }

    private List<List<String>> findActualPrompts() {

        List<List<String>> allInfo = new ArrayList<>();

        promptService.getActualPrompts(Instant.now().plusSeconds(86400), Instant.now().plusSeconds(2 * 86400))
                .ifPresent(prompts -> prompts.forEach(prompt -> {
                    Long chatId = userService.getChatId(prompt.getUserName());
                    if (chatId != null) {
                        List<String> info = new ArrayList<>();
                        info.add(userService.getChatId(prompt.getUserName()).toString());
                        info.add(prompt.getDate().toString());
                        info.add(prompt.getTaskDescription());

                        allInfo.add(info);
                    }
                    promptService.updatePrompt(prompt.getTaskDescription(), prompt.getDate().plusSeconds(computeReminderFrequency(prompt.getReminderFrequency())));
                }));
        return allInfo;

    }

    private Long computeReminderFrequency(String frequency) {
        // TODO изменить ReminderFrequency на лонг или сделать парсинг!!
        // тут должен использоваться парсинг сервис вместо этого метода


        return 0L;
    }
}
