package ru.telegrambot.handler;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.DataBinder;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static ru.telegrambot.constant.state.ChatStateType.ACTUAL_STATE;
import static ru.telegrambot.constant.state.ChatStateType.SET_REMINDER_FREQUENCY;


@Slf4j
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class MessageHandler {

    final StateControlService stateControlService;
    final UserService userService;
    final PromptService promptService;

    final InlineKeyboard inlineKeyboard;
    final ReplyKeyboard replyKeyboard;

    final AdminValidator personValidator;

    /**
     * Обработка входящих входящих текстовых сообщений
     * @param update объект содержащий текстовое сообщение от пользователя
     * @return объект SendMessage содержащий текстовый ответ пользователю на запрос
     */
    public SendMessage answerMessage(Update update) {

        Long chatId = update.getMessage().getChatId();
        String input = update.getMessage().getText();

        String userName = update.getMessage().getFrom().getUserName();
        boolean isAdmin = checkIsAdmin(userName);

        return switch (input) {
            case "/start" -> getStartMessage(chatId, userName, isAdmin);
            case "/help" -> getHelpMessage(chatId, isAdmin);
            case "/timezone" -> getLocationMessage(chatId, userName, isAdmin);
            case "Создать напоминание" -> addPromptMessage(chatId);
            case "Получить все напоминания" -> getPromptsMessage(chatId, userName, isAdmin);
            case "Удалить напоминание" -> deletePromptMessage(chatId);
            case "Обновить напоминание" -> updatePromptMessage(chatId);
            case "Обновить описание" -> updateDescriptionMessage(chatId);
            case "Обновить ближайшую дату" -> updateDataMessage(chatId, userName);
            case "Обновить частоту напоминания" -> updateRemindingFrequencyMessage(chatId, userName);
            case "Вернуться в основное меню" -> getReturnMessage(chatId);
            case "Показать всех пользователей" -> getAllUsersMessage(chatId, userName);
            case "Добавить пользователя" -> getAddUserMessage(chatId, userName);
            case "Удалить пользователя" -> getDeleteUserMessage(chatId, userName);
            case "Обновить И.Ф. пользователя" -> getUpdateUserMessage(chatId, userName);
            case "❎" -> getCancellationMessage(chatId, userName);
            case "✅" -> getConfirmationMessage(chatId, userName);
            default -> getUserInfo(chatId, userName, input);
        };

    }


    private SendMessage getUpdateUserMessage(Long chatId, String userName) {

        stateControlService.addState(userName, ChatStateType.UPDATE_USER);

        try {
            return createSendMessageWithInlineKeyboard(chatId.toString(),
                    BotMessageTemplate.CHOOSE_USER_MESSAGE.getDescription(),
                    inlineKeyboard.getInlineMessageKeyboardWithNames());
        } catch (BotException e) {
            return createSendMessageWithoutKeyboard(chatId.toString(),
                    BotMessageTemplate.USER_LIST_ERROR.getDescription());
        }

    }

    private SendMessage updateRemindingFrequencyMessage(Long chatId, String userName) {
        stateControlService.addState(userName, SET_REMINDER_FREQUENCY);

        return createSendMessageWithoutKeyboard(chatId.toString(),
                BotMessageTemplate.ADD_PROMPT_REMIND_MESSAGE.getDescription());
    }

    private SendMessage updateDataMessage(Long chatId, String userName) {
        stateControlService.addState(userName, ChatStateType.UPDATE_PROMPT_DATE);

        String updateResult = promptService.updateState(PromptState.UPDATE,
                PromptState.SET_PROMPT_DATE);

        return createSendMessageWithoutKeyboard(chatId.toString(),
                updateResult);

    }

    private SendMessage updateDescriptionMessage(Long chatId) {
        return createSendMessageWithoutKeyboard(chatId.toString(),
                BotMessageTemplate.ADD_PROMPT_MESSAGE.getDescription());
    }

    private SendMessage getStartMessage(Long chatId, String userName, boolean isAdmin) {

        try {
            userService.saveUserChatId(userName, chatId);
        } catch (BotException e) {
            log.error("Fail to find user with username: {}", userName, e);
            return createSendMessageWithoutKeyboard(chatId.toString(),
                    BotMessageTemplate.ERROR_FIND_USER.getDescription());
        }

        if (isAdmin) {
            return createSendMessageWithReplyKeyboard(chatId.toString(),
                    BotMessageTemplate.START_MESSAGE_ADMIN.getDescription(),
                    replyKeyboard.getAdminUserMenuKeyboard());
        } else {
            return createSendMessageWithReplyKeyboard(chatId.toString(),
                    BotMessageTemplate.START_MESSAGE_USER.getDescription(),
                    replyKeyboard.getUserMenuKeyboard());
        }
    }

    private SendMessage getHelpMessage(Long chatId, boolean isAdmin) {

        if (isAdmin) {
            return createSendMessageWithReplyKeyboard(chatId.toString(),
                    BotMessageTemplate.HELP_MESSAGE_ADMIN.getDescription(),
                    replyKeyboard.getAdminUserMenuKeyboard());
        } else {
            return createSendMessageWithReplyKeyboard(chatId.toString(),
                    BotMessageTemplate.HELP_MESSAGE_USER.getDescription(),
                    replyKeyboard.getUserMenuKeyboard());
        }
    }

    private SendMessage getLocationMessage(Long chatId, String userName, boolean isAdmin) {

        stateControlService.addState(userName, ChatStateType.SAVE_LOCALE);

        if (isAdmin) {
            return createSendMessageWithReplyKeyboard(chatId.toString(),
                    BotMessageTemplate.SET_TIME_ZONE_MESSAGE.getDescription(),
                    replyKeyboard.getAdminUserMenuKeyboard());
        } else {
            return createSendMessageWithReplyKeyboard(chatId.toString(),
                    BotMessageTemplate.SET_TIME_ZONE_MESSAGE.getDescription(),
                    replyKeyboard.getUserMenuKeyboard());
        }
    }

    private SendMessage updatePromptMessage(Long chatId) {

        String username = stateControlService.getState().getUserName();
        stateControlService.addState(username, ChatStateType.UPDATE_PROMPT);

        return createSendMessageWithInlineKeyboard(chatId.toString(),
                BotMessageTemplate.CHOOSE_PROMPT_MESSAGE.getDescription(),
                inlineKeyboard.getInlineMessageKeyboardWithPrompts(username));
    }

    private SendMessage deletePromptMessage(Long chatId) {

        String username = stateControlService.getState().getUserName();
        stateControlService.addState(username, ChatStateType.DELETE_PROMPT);

        return createSendMessageWithInlineKeyboard(chatId.toString(),
                BotMessageTemplate.CHOOSE_PROMPT_MESSAGE.getDescription(),
                inlineKeyboard.getInlineMessageKeyboardWithPrompts(username));
    }

    private SendMessage addPromptMessage(Long chatId) {

        stateControlService.updateStateForLastUser(ChatStateType.ADD_PROMPT);

        return createSendMessageWithoutKeyboard(chatId.toString(),
                BotMessageTemplate.ADD_PROMPT_MESSAGE.getDescription());
    }

    private SendMessage getPromptsMessage(Long chatId, String userName, boolean isAdmin) {

        if (isAdmin) {
            return createSendMessageWithReplyKeyboard(chatId.toString(),
                    getAllPromptsDescription(userName),
                    replyKeyboard.getAdminUserMenuKeyboard());
        } else {
            return createSendMessageWithReplyKeyboard(chatId.toString(),
                    getAllPromptsDescription(userName),
                    replyKeyboard.getUserMenuKeyboard());
        }
    }

    private SendMessage getReturnMessage(Long chatId) {
        return createSendMessageWithReplyKeyboard(chatId.toString(),
                BotMessageTemplate.CHOOSE_FROM_MENU_MESSAGE.getDescription(),
                replyKeyboard.getAdminUserMenuKeyboard());
    }

    private SendMessage getAllUsersMessage(Long chatId, String userName) {

        stateControlService.addState(userName, ChatStateType.CHOOSE_ACTION);

        try {
            return createSendMessageWithInlineKeyboard(chatId.toString(),
                    BotMessageTemplate.CHOOSE_USER_MESSAGE.getDescription(),
                    inlineKeyboard.getInlineMessageKeyboardWithNames());
        } catch (BotException e) {
            return createSendMessageWithoutKeyboard(chatId.toString(),
                    BotMessageTemplate.USER_LIST_ERROR.getDescription());
        }
    }

    private SendMessage getAddUserMessage(Long chatId, String userName) {

        stateControlService.addState(userName, ChatStateType.ADD_USER);

        return createSendMessageWithoutKeyboard(chatId.toString(),
                BotMessageTemplate.ADD_USER_MESSAGE.getDescription());
    }

    private SendMessage getDeleteUserMessage(Long chatId, String userName) {

        stateControlService.addState(userName, ChatStateType.DELETE_USER);

        try {
            return createSendMessageWithInlineKeyboard(chatId.toString(),
                    BotMessageTemplate.CHOOSE_USER_MESSAGE.getDescription(),
                    inlineKeyboard.getInlineMessageKeyboardWithNames());
        } catch (BotException e) {
            return createSendMessageWithoutKeyboard(chatId.toString(),
                    BotMessageTemplate.USER_LIST_ERROR.getDescription());
        }
    }

    private SendMessage getCancellationMessage(Long chatId, String userName) {

        stateControlService.addState(userName, ChatStateType.ACTUAL_STATE);

        return createSendMessageWithReplyKeyboard(chatId.toString(),
                BotMessageTemplate.CANCELLATION_MESSAGE.getDescription(),
                replyKeyboard.getAdminUserMenuKeyboard());
    }

    private SendMessage getConfirmationMessage(Long chatId, String userName) {

        ChatState chatState = stateControlService.getState();

        switch (chatState.getChatState()) {
            case "DELETE_USER" -> {
                try {
                    userService.deleteUser(chatState.getUserName());
                } catch (BotException e) {
                    log.error("Fail to find user with username: {}", chatState.getUserName(), e);
                }
            }
            case "DELETE_PROMPT" -> promptService.deletePrompt(
                    promptService.getByState(PromptState.DELETE));
            default -> {
                return createSendMessageWithReplyKeyboard(chatId.toString(),
                        BotMessageTemplate.ERROR_STATE_MESSAGE.getDescription(),
                        replyKeyboard.getAdminUserMenuKeyboard());
            }
        }

        stateControlService.addState(userName, ChatStateType.ACTUAL_STATE);

        return createSendMessageWithReplyKeyboard(chatId.toString(),
                BotMessageTemplate.CONFIRMATION_MESSAGE.getDescription(),
                replyKeyboard.getAdminUserMenuKeyboard());

    }

    // TODO нужен рефакторинг! достать все и перекинуть возможно в UserInputParseService
    private SendMessage getUserInfo(Long chatId, String userName, String input) {
        SendMessage sendMessage;

        ChatState chatState = stateControlService.getState();
        String activeUser = chatState.getUserName();

        switch (ChatStateType.valueOf(chatState.getChatState())) {
            case ADD_USER:
            case UPDATE_USER:
                String[] words = input.split(" ");
                if (words.length == 3) {
                    userService.saveUserNames(words);
                    stateControlService.addState(userName, ChatStateType.ACTUAL_STATE);
                    sendMessage = new SendMessage(chatId.toString(), BotMessageTemplate.SUCCESS_ADD_USER_INFO_MESSAGE.getDescription());
                } else if (words.length == 2) {
                    try {
                        userService.updateUserNames(chatState.getUserName(), words);
                    } catch (Exception e) {
                        return new SendMessage(chatId.toString(), BotMessageTemplate.INVALID_INPUT_MESSAGE.getDescription());
                    }
                    stateControlService.addState(userName, ChatStateType.ACTUAL_STATE);
                    sendMessage = new SendMessage(chatId.toString(), BotMessageTemplate.SUCCESS_ADD_USER_INFO_MESSAGE.getDescription());
                } else {
                    sendMessage = new SendMessage(chatId.toString(), BotMessageTemplate.INVALID_FORM_MESSAGE.getDescription());
                }
                break;

            case SAVE_LOCALE:

                if (input.matches("^[+-](([0-1][0-9]):([0-5][0-9])|12:00)$")) {
                    try {
                        userService.saveTimeZone(userName, "GMT" + input);
                    } catch (BotException e) {
                        return new SendMessage(chatId.toString(), BotMessageTemplate.ERROR_FIND_USER.getDescription());
                    }
                    stateControlService.addState(userName, ChatStateType.ACTUAL_STATE);
                    sendMessage = new SendMessage(chatId.toString(), BotMessageTemplate.SUCCESS_ADD_LOCATION_MESSAGE.getDescription());
                } else {
                    sendMessage = new SendMessage(chatId.toString(), BotMessageTemplate.INVALID_FORM_MESSAGE.getDescription());
                }
                break;

            case ADD_PROMPT:

                if (!input.isBlank() && !input.isEmpty()) {

                    try {
                        User user = userService.getUserByUserName(activeUser);
                        List<Prompt> prompts = user.getPrompts();

                        Prompt prompt = new Prompt();
                        prompt.setTaskDescription(input);
                        prompt.setPromptStateType(PromptState.SET_PROMPT_DATE.name());
                        prompts.add(prompt);
                        user.setPrompts(prompts);

                        userService.updateUserInfo(user);

                        stateControlService.addState(activeUser, ChatStateType.SET_PROMPT_DATE);

                        log.info("Prompt info add successful. New info: {}", input);
                    } catch (Exception e) {
                        log.error("Fail to add prompt for user with username: {}", activeUser, e);
                        return createSendMessageWithoutKeyboard(chatId.toString(),
                                (BotMessageTemplate.ERROR_FIND_USER.getDescription() + activeUser));
                    }

                    return createSendMessageWithoutKeyboard(chatId.toString(), BotMessageTemplate.ADD_PROMPT_DATE_MESSAGE.getDescription());
                } else {
                    return createSendMessageWithoutKeyboard(chatId.toString(), BotMessageTemplate.INVALID_INPUT_MESSAGE.getDescription());
                }

            case UPDATE_PROMPT:

                if (!input.isBlank() && !input.isEmpty()) {

                    try {

                        Prompt prompt = promptService.getByState(PromptState.UPDATE);
                        prompt.setTaskDescription(input);
                        prompt.setPromptStateType(PromptState.ACTUAL.name());
                        promptService.savePrompt(prompt);

                        stateControlService.addState(userName, ACTUAL_STATE);

                        log.info("Prompt info add successful. New info: {}", input);
                    } catch (Exception e) {
                        log.error("Fail to find prompt with state: {}", PromptState.UPDATE.name(), e);
                    }

                    sendMessage = new SendMessage(chatId.toString(), BotMessageTemplate.SUCCESS_ADD_NEW_INFO_MESSAGE.getDescription());
                } else {
                    sendMessage = new SendMessage(chatId.toString(), BotMessageTemplate.INVALID_INPUT_MESSAGE.getDescription());
                }
                break;
            case UPDATE_PROMPT_DATE:
                if (promptService.saveDate(input, PromptState.ACTUAL)) {
                    stateControlService.addState(userName, ChatStateType.ACTUAL_STATE);

                    sendMessage = new SendMessage(chatId.toString(), BotMessageTemplate.SUCCESS_ADD_NEW_INFO_MESSAGE.getDescription());
                } else {
                    sendMessage = new SendMessage(chatId.toString(), BotMessageTemplate.INVALID_DATE_FORMAT.getDescription());
                }
                break;

            case SET_PROMPT_DATE:

                if (promptService.saveDate(input, PromptState.SET_REMINDER_FREQUENCY)) {

                    stateControlService.addState(activeUser, ChatStateType.SET_REMINDER_FREQUENCY);

                    return createSendMessageWithoutKeyboard(chatId.toString(),
                            BotMessageTemplate.ADD_PROMPT_REMIND_MESSAGE.getDescription());
                } else {
                    return createSendMessageWithoutKeyboard(chatId.toString(), BotMessageTemplate.INVALID_DATE_FORMAT.getDescription());
                }

            case SET_REMINDER_FREQUENCY:

                if (!input.isBlank() && !input.isEmpty()) {
                    Prompt prompt = promptService.getByState(PromptState.SET_REMINDER_FREQUENCY);

                    prompt.setReminderFrequency(input); // TODO Try catch
                    prompt.setPromptStateType(PromptState.ACTUAL.name());
                    promptService.savePrompt(prompt);

                    stateControlService.addState(userName, ChatStateType.ACTUAL_STATE);

                }
//                parseService.parseReminderFrequency(input);

//                return createSendMessageWithReplyKeyboard(chatId.toString(),
//                        BotMessageTemplate.ADD_PROMPT_SUCCESS_MESSAGE.getDescription(),
//                        replyKeyboard.getRemindingKeyboard());
                sendMessage = new SendMessage(chatId.toString(), BotMessageTemplate.SUCCESS_ADD_PROMPT_MESSAGE.getDescription());
                break;

            default:
                sendMessage = new SendMessage(chatId.toString(), BotMessageTemplate.INVALID_FORM_MESSAGE.getDescription());
                break;
        }

        sendMessage.enableMarkdown(true);
        sendMessage.setReplyMarkup(replyKeyboard.getAdminUserMenuKeyboard());
        return sendMessage;

    }

    private SendMessage createSendMessageWithoutKeyboard(String chatId, String message) {
        SendMessage sendMessage = new SendMessage(chatId, message);
        sendMessage.enableMarkdown(true);
        sendMessage.setReplyMarkup(new ReplyKeyboardRemove(true));

        return sendMessage;
    }

    private SendMessage createSendMessageWithReplyKeyboard(String chatId, String message, ReplyKeyboardMarkup replyKeyboardMarkup) {

        SendMessage sendMessage = new SendMessage(chatId, message);
        sendMessage.enableMarkdown(true);
        sendMessage.setReplyMarkup(replyKeyboardMarkup);

        return sendMessage;
    }

    private SendMessage createSendMessageWithInlineKeyboard(String chatId, String message, InlineKeyboardMarkup inlineKeyboardMarkup) {

        SendMessage sendMessage = new SendMessage(chatId, message);
        sendMessage.enableMarkdown(true);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);

        return sendMessage;
    }

    private String getAllPromptsDescription(String userName) {
        ChatState chatState = stateControlService.getState();

        try {
            User user = userService.getUserByUserName(chatState.getUserName());
            List<Prompt> prompts = user.getPrompts();

            if (!prompts.isEmpty()) {
                StringBuffer parsedPrompts = new StringBuffer();

                prompts.stream()
                        .filter(prompt -> prompt.getPromptStateType().equals(PromptState.ACTUAL.name()))
                        .forEach(prompt -> {
                            String date;
                            try {
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                                Date d = sdf.parse(prompt.getDate().toString());

                                SimpleDateFormat properFullFormat = new SimpleDateFormat("dd-MM-yyyy  HH:mm");
                                date = properFullFormat.format(d);
                            } catch (ParseException e) {
                                date = BotMessageTemplate.MISSING_DATE.getDescription();
                                log.error("Fail to parse date. Date is missing");
                            }
                            parsedPrompts.append(String.join(" ", "*" + date + "*", prompt.getTaskDescription(), "\n"));
                        });

                stateControlService.addState(userName, ChatStateType.ACTUAL_STATE);
                return parsedPrompts.toString();
            } else {
                stateControlService.addState(userName, ChatStateType.ACTUAL_STATE);
                return BotMessageTemplate.NO_PROMPTS_MESSAGE.getDescription();
            }

        } catch (Exception e) {
            log.error("Fail to find user with username: {}", chatState.getUserName(), e);
            return BotMessageTemplate.UNKNOWN_ERROR.getDescription();
        }

    }

    private boolean checkIsAdmin(String userName) {
        final DataBinder dataBinder = new DataBinder(userName);
        dataBinder.addValidators(personValidator);
        dataBinder.validate();

        return (!dataBinder.getBindingResult().hasErrors());
    }

}