package ru.telegrambot.handler;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.DataBinder;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import ru.telegrambot.constant.CallbackDataParts;
import ru.telegrambot.constant.message.BotMessageTemplate;
import ru.telegrambot.constant.state.ChatStateType;
import ru.telegrambot.constant.state.PromptState;
import ru.telegrambot.entity.ChatState;
import ru.telegrambot.entity.Prompt;
import ru.telegrambot.entity.User;
import ru.telegrambot.exeption.BotException;
import ru.telegrambot.keyboard.InlineKeyboard;
import ru.telegrambot.keyboard.ReplyKeyboard;
import ru.telegrambot.service.PromptService;
import ru.telegrambot.service.StateControlService;
import ru.telegrambot.service.UserService;
import ru.telegrambot.validation.AdminValidator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Consumer;

import static ru.telegrambot.constant.message.BotMessageTemplate.CHOOSE_PROMPT_ACTIVITY;
import static ru.telegrambot.constant.message.BotMessageTemplate.DELETE_USER_MESSAGE;
import static ru.telegrambot.constant.message.BotMessageTemplate.ERROR_FIND_USER;
import static ru.telegrambot.constant.message.BotMessageTemplate.INVALID_FORM_MESSAGE;
import static ru.telegrambot.constant.message.BotMessageTemplate.UNKNOWN_ERROR;

@Slf4j
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class CallbackQueryHandler {

    final StateControlService stateControlService;
    final UserService userService;
    final PromptService promptService;

    final ReplyKeyboard replyKeyboard;
    final InlineKeyboard inlineKeyboard;

    final AdminValidator personValidator;

    public static Long page = 0L;

    /**
     * Обработка входящих объектов CallbackQuery
     *
     * @param callbackQuery входящий запрос обратного вызова от кнопки обратного вызова на встроенной инлайн-клавиатуре
     * @return объект SendMessage содержащий текстовый ответ пользователю на запрос
     */
    public BotApiMethod<?> processCallbackQuery(CallbackQuery callbackQuery) {
        final String chatId = callbackQuery.getMessage().getChatId().toString();
        final Integer messageId = callbackQuery.getMessage().getMessageId();
        String data = callbackQuery.getData();
        ChatState chatState = stateControlService.getState();
        boolean isAdmin = checkIsAdmin(callbackQuery.getFrom().getUserName());

        if (data.startsWith(CallbackDataParts.USER_NAME_.name())) {
            String userName = data.substring(CallbackDataParts.USER_NAME_.name().length());

            switch (chatState.getChatState()) {
                case "DELETE_USER" -> {
                    try {
                        stateControlService.addState(userName, ChatStateType.DELETE_USER);
                        return SendMessage.builder()
                                .chatId(chatId)
                                .text(DELETE_USER_MESSAGE.getDescription() + getFullName(userName))
                                .replyMarkup(inlineKeyboard.getInlineMessageKeyboardWithConfirmation())
                                .build();
                    } catch (Exception e) {
                        log.error("Fail to get full name for user with username: {}. User not found", userName, e);
                        return SendMessage.builder()
                                .chatId(chatId)
                                .text(ERROR_FIND_USER.getDescription() + userName)
                                .replyMarkup(replyKeyboard.getAdminUserMenuKeyboard())
                                .build();
                    }
                }
                case "UPDATE_USER" -> {
                    stateControlService.addState(userName, ChatStateType.UPDATE_USER);
                    return SendMessage.builder()
                            .chatId(chatId)
                            .text(BotMessageTemplate.UPDATE_USER_MESSAGE.getDescription())
                            .replyMarkup(new ReplyKeyboardRemove(true))
                            .build();
                }
                default -> {
                    page = 0L;
                    stateControlService.addState(userName, ChatStateType.CHOOSE_ACTION);
                    try {
                        Prompt prompt = getChosenPrompt();

                        return EditMessageText.builder()
                                .messageId(messageId)
                                .chatId(chatId)
                                .text(String.format("<b>Дата: %s</b>\n\n%s",
                                        parseDate(prompt.getDate()),
                                        prompt.getTaskDescription()))
                                .parseMode("html")
                                .replyMarkup(inlineKeyboard.getInlineMessageKeyboardWithPrompts(chatState.getUserName(), page))
                                .build();

                    } catch (NullPointerException | NoSuchElementException e) {
                        log.info("Prompts for user with username: {} not found", userName, e);
                        return EditMessageText.builder()
                                .messageId(messageId)
                                .chatId(chatId)
                                .text(BotMessageTemplate.NO_PROMPTS_MESSAGE.getDescription())
                                .replyMarkup(inlineKeyboard.getInlineMessageKeyboardWithActions(isAdmin))
                                .build();
                    }

                }
            }
// TODO добавить обработку методов обновления напоминания
        } else if (data.startsWith(CallbackDataParts.PROMPT_.name())) {
            String promptAction = data.substring(CallbackDataParts.PROMPT_.name().length());

            switch (promptAction) {
                case "UPDATE_DATE":
                    stateControlService.updateStateForLastUser(ChatStateType.UPDATE_PROMPT_DATE);

                    return EditMessageText.builder()
                            .messageId(messageId)
                            .chatId(chatId)
                            .text(promptService.updateState(PromptState.UPDATE, PromptState.SET_PROMPT_DATE))
                            .build();

                case "UPDATE_DESCRIPTION":
                    stateControlService.updateStateForLastUser(ChatStateType.UPDATE_PROMPT);

                    return EditMessageText.builder()
                            .messageId(messageId)
                            .chatId(chatId)
                            .text(BotMessageTemplate.ADD_PROMPT_MESSAGE.getDescription())
                            .build();

                case "UPDATE_REMINDING_FREQUENCY":
                    stateControlService.updateStateForLastUser(ChatStateType.SET_REMINDER_FREQUENCY);

                    return EditMessageText.builder()
                            .messageId(messageId)
                            .chatId(chatId)
                            .text(BotMessageTemplate.ADD_PROMPT_REMIND_MESSAGE.getDescription())
                            .build();

                default:
                    return SendMessage.builder()
                            .chatId(chatId)
                            .text(BotMessageTemplate.ILLEGAL_STATE_MESSAGE.getDescription())
                            .replyMarkup(replyKeyboard.getAdminUserMenuKeyboard())
                            .build();
            }
        } else if (data.startsWith(CallbackDataParts.ACTION_.name())) {

            String action = data.substring(CallbackDataParts.ACTION_.name().length());


            switch (action) {
                case "ALL_PROMPTS":
                case "NEXT":
                case "PREVIOUS":
                    changePage(action);
                    Prompt prompt = getChosenPrompt();

                    return EditMessageText.builder()
                            .messageId(messageId)
                            .chatId(chatId)
                            .text(String.format("<b>Дата: %s</b>\n\n%s",
                                    parseDate(prompt.getDate()),
                                    prompt.getTaskDescription()))
                            .parseMode("html")
                            .replyMarkup(inlineKeyboard.getInlineMessageKeyboardWithPrompts(chatState.getUserName(), page))
                            .build();

                case "CHOOSE":
                    return EditMessageText.builder()
                            .messageId(messageId)
                            .chatId(chatId)
                            .text(CHOOSE_PROMPT_ACTIVITY.getDescription())
                            .replyMarkup(inlineKeyboard.getInlineMessageKeyboardWithActions(isAdmin))
                            .build();

                case "ADD_PROMPT":
                    stateControlService.updateStateForLastUser(ChatStateType.ADD_PROMPT);

                    return EditMessageText.builder()
                            .messageId(messageId)
                            .chatId(chatId)
                            .text(BotMessageTemplate.ADD_PROMPT_MESSAGE.getDescription())
                            .build();

                case "DELETE_PROMPT":

                    prompt = getChosenPrompt();
                    String username = stateControlService.getState().getUserName();

                    try {
                        stateControlService.addState(username, ChatStateType.DELETE_PROMPT);
                        promptService.updateState(prompt.getTaskDescription(), PromptState.DELETE);
                        return EditMessageText.builder()
                                .messageId(messageId)
                                .chatId(chatId)
                                .text(String.format("%s\n<b>Имя: %s\nДата: %s</b>\n\n%s",
                                        BotMessageTemplate.DELETE_PROMPT_MESSAGE.getDescription(),
                                        getFullName(username),
                                        parseDate(prompt.getDate()),
                                        prompt.getTaskDescription()))
                                .parseMode("html")
                                .replyMarkup(inlineKeyboard.getInlineMessageKeyboardWithConfirmation())
                                .build();

                    } catch (Exception e) {
                        log.error("Fail to get full name for user with username: . User not found", e);
                        return SendMessage.builder()
                                .chatId(chatId)
                                .text(ERROR_FIND_USER.getDescription())
                                .replyMarkup(replyKeyboard.getAdminUserMenuKeyboard())
                                .build();
                    }

                case "UPDATE_PROMPT":
                    String activeUser = stateControlService.getState().getUserName();
                    prompt = getChosenPrompt();
                    stateControlService.addState(activeUser, ChatStateType.UPDATE_PROMPT);
                    promptService.updateState(prompt.getTaskDescription(), PromptState.UPDATE);

                    try {
                        return EditMessageText.builder()
                                .messageId(messageId)
                                .chatId(chatId)
                                .text(String.format("%s\n\n<b>Имя: %s\nДата: %s</b>\n\n%s",
                                        BotMessageTemplate.CHOOSE_ACTIVITY.getDescription(),
                                        getFullName(activeUser),
                                        parseDate(prompt.getDate()),
                                        prompt.getTaskDescription()))
                                .parseMode("html")
                                .replyMarkup(inlineKeyboard.getInlineMessageKeyboardWithUpdates())
                                .build();
                    } catch (Exception e) {
                        log.error("Fail to get full name for user with username: . User not found", e);
                        return SendMessage.builder()
                                .chatId(chatId)
                                .text(ERROR_FIND_USER.getDescription())
                                .replyMarkup(replyKeyboard.getAdminUserMenuKeyboard())
                                .build();
                    }

                case "CONFIRM":
                    chatState = stateControlService.getState();

                    switch (chatState.getChatState()) {
                        case "DELETE_USER" -> {
                            return SendMessage.builder()
                                    .chatId(chatId)
                                    .text(userService.deleteUser(chatState.getUserName()))
                                    .replyMarkup(replyKeyboard.getAdminUserMenuKeyboard())
                                    .build();
                        }
                        case "DELETE_PROMPT" -> {
                            promptService.deletePrompt(
                                    promptService.getByState(PromptState.DELETE));

                            return SendMessage.builder()
                                    .chatId(chatId)
                                    .text(BotMessageTemplate.CONFIRMATION_MESSAGE.getDescription())
                                    .replyMarkup(replyKeyboard.getAdminUserMenuKeyboard())
                                    .build();
                        }
                        default -> {
                            return SendMessage.builder()
                                    .chatId(chatId)
                                    .text(BotMessageTemplate.ERROR_STATE_MESSAGE.getDescription())
                                    .replyMarkup(replyKeyboard.getAdminUserMenuKeyboard())
                                    .build();
                        }
                    }

                case "CANCEL":
                    stateControlService.updateStateForLastUser(ChatStateType.ACTUAL_STATE);
                    promptService.updateState(PromptState.DELETE, PromptState.ACTUAL);
                    return SendMessage.builder()
                            .chatId(chatId)
                            .text(BotMessageTemplate.CANCELLATION_MESSAGE.getDescription())
                            .replyMarkup(replyKeyboard.getAdminUserMenuKeyboard())
                            .build();

                case "USER_MENU":
                    return SendMessage.builder()
                            .chatId(chatId)
                            .text(BotMessageTemplate.CHOOSE_FROM_MENU_MESSAGE.getDescription())
                            .replyMarkup(replyKeyboard.getAdminUserMenuKeyboard())
                            .build();

                default:
                    return SendMessage.builder()
                            .chatId(chatId)
                            .text(INVALID_FORM_MESSAGE.getDescription())
                            .replyMarkup(replyKeyboard.getAdminUserMenuKeyboard())
                            .build();
            }
        } else {
            return SendMessage.builder()
                    .chatId(chatId)
                    .text(UNKNOWN_ERROR.getDescription())
                    .replyMarkup(replyKeyboard.getAdminUserMenuKeyboard())
                    .build();
        }
    }

    private Prompt getChosenPrompt() {

        List<Prompt> prompts = promptService.getAllByState(PromptState.ACTUAL);
        prompts.sort((p1, p2) -> {
            if (p1.getDate() == null || p2.getDate() == null)
                return 0;
            return p1.getDate().compareTo(p2.getDate());
        });

        return prompts.get(Math.toIntExact(page));

    }

    private String parseDate(LocalDateTime date) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy  HH:mm");
            return date.format(formatter);
        } catch (NullPointerException e) {
            log.error("Fail to parse date. Date is missing");
            return BotMessageTemplate.MISSING_DATE.getDescription();
        }
    }

    private String getFullName(String userName) throws BotException {
        User user = userService.getUserByUserName(userName);

        return (user.getFirstName() != null && user.getSecondName() != null) ?
                String.join(" ", user.getFirstName(), user.getSecondName()) : userName;
    }

    private boolean checkIsAdmin(String userName) {
        final DataBinder dataBinder = new DataBinder(userName);
        dataBinder.addValidators(personValidator);
        dataBinder.validate();

        return (!dataBinder.getBindingResult().hasErrors());
    }

    private void changePage(String action) {
        Map<String, Consumer<String>> strategyMap = Map.of(
                "ALL_PROMPTS", (value) -> page = 0L,
                "NEXT", (value) -> ++page,
                "PREVIOUS", (value) -> --page);

        Optional.ofNullable(strategyMap.get(action)).get().accept(action);
    }
}
