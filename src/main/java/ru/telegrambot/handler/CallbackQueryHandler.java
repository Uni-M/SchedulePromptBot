package ru.telegrambot.handler;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import ru.telegrambot.constant.CallbackDataParts;
import ru.telegrambot.constant.state.ChatStateType;
import ru.telegrambot.constant.state.PromptState;
import ru.telegrambot.entity.ChatState;
import ru.telegrambot.entity.User;
import ru.telegrambot.keyboard.ReplyKeyboard;
import ru.telegrambot.service.PromptService;
import ru.telegrambot.service.StateControlService;
import ru.telegrambot.service.UserService;

import static ru.telegrambot.constant.message.BotExceptionMessage.FAIL_GET_FULL_NAME;
import static ru.telegrambot.constant.message.BotMessageTemplate.CHOOSE_ACTIVITY;
import static ru.telegrambot.constant.message.BotMessageTemplate.CHOOSE_PROMPT_ACTIVITY;
import static ru.telegrambot.constant.message.BotMessageTemplate.DELETE_PROMPT_MESSAGE;
import static ru.telegrambot.constant.message.BotMessageTemplate.DELETE_USER_MESSAGE;
import static ru.telegrambot.constant.message.BotMessageTemplate.ERROR_FIND_USER;
import static ru.telegrambot.constant.message.BotMessageTemplate.ILLEGAL_STATE_MESSAGE;
import static ru.telegrambot.constant.message.BotMessageTemplate.UNKNOWN_ERROR;
import static ru.telegrambot.constant.message.BotMessageTemplate.UPDATE_USER_MESSAGE;

@Slf4j
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class CallbackQueryHandler {

    final StateControlService stateControlService;
    final UserService userService;
    final PromptService promptService;

    final ReplyKeyboard replyKeyboard;

    // Ответ на инлайн-клавиатуру
    public SendMessage processCallbackQuery(CallbackQuery callbackQuery) {

        final String chatId = callbackQuery.getMessage().getChatId().toString();
        String data = callbackQuery.getData();
        ChatState chatState = stateControlService.getState();

        if (data.startsWith(CallbackDataParts.USER_NAME_.name())) {
            String userName = data.substring(CallbackDataParts.USER_NAME_.name().length());

            switch (chatState.getChatState()) {
                case "DELETE_USER":
                    stateControlService.addState(userName, ChatStateType.DELETE_USER);
                    return createSendMessageWithReplyKeyboard(chatId,
                            getReplyMessage(userName),
                            replyKeyboard.getDeletionConfirmationKeyboard());
                case "UPDATE_USER":
                    stateControlService.addState(userName, ChatStateType.UPDATE_USER);
                    return createSendMessageWithoutKeyboard(chatId,
                            UPDATE_USER_MESSAGE.getDescription());
                default:
                    stateControlService.addState(userName, ChatStateType.CHOOSE_ACTION);
                    return createSendMessageWithReplyKeyboard(chatId,
                            CHOOSE_ACTIVITY.getDescription(), // TODO добавить полное имя
                            replyKeyboard.getAdminPromptMenuKeyboard());
            }

        } else if (data.startsWith(CallbackDataParts.PROMPT_.name())) {
            String promptDescription = data.substring(CallbackDataParts.PROMPT_.name().length());

            switch (chatState.getChatState()) {
                case "DELETE_PROMPT":
                    promptService.updateState(promptDescription, PromptState.DELETE);
                    return createSendMessageWithReplyKeyboard(chatId,
                            DELETE_PROMPT_MESSAGE.getDescription() + promptDescription,
                            replyKeyboard.getDeletionConfirmationKeyboard());
                case "UPDATE_PROMPT":
                    promptService.updateState(promptDescription, PromptState.UPDATE);
                    return createSendMessageWithReplyKeyboard(chatId,
                            CHOOSE_PROMPT_ACTIVITY.getDescription() + promptDescription,
                            replyKeyboard.getUpdatePromptKeyboard());
                default:
                    return createSendMessageWithReplyKeyboard(chatId,
                            ILLEGAL_STATE_MESSAGE.getDescription(),
                            replyKeyboard.getAdminUserMenuKeyboard());
            }

        } else {
            return createSendMessageWithReplyKeyboard(chatId,
                    UNKNOWN_ERROR.getDescription(),
                    replyKeyboard.getAdminUserMenuKeyboard());
        }

    }

    private SendMessage createSendMessageWithReplyKeyboard(String chatId, String message, ReplyKeyboardMarkup replyKeyboardMarkup) {

        SendMessage sendMessage = new SendMessage(chatId, message);
        sendMessage.enableMarkdown(true);
        sendMessage.setReplyMarkup(replyKeyboardMarkup);

        return sendMessage;
    }

    private SendMessage createSendMessageWithoutKeyboard(String chatId, String message) {
        SendMessage sendMessage = new SendMessage(chatId, message);
        sendMessage.enableMarkdown(true);
        sendMessage.setReplyMarkup(new ReplyKeyboardRemove(true));

        return sendMessage;
    }

    // TODO получение полного имени в отельном методе
    private String getReplyMessage(String userName) {

        User user;
        try {
            user = userService.getUserByUserName(userName);
            String replyName = (user.getFirstName() != null && user.getSecondName() != null) ?
                    String.join(" ", user.getFirstName(), user.getSecondName()) :
                    userName;
            return DELETE_USER_MESSAGE.getDescription() + replyName;
        } catch (Exception e) {
            log.error(FAIL_GET_FULL_NAME.getMessage(),userName, e);
        }

        return ERROR_FIND_USER.getDescription()+userName;

    }

}
