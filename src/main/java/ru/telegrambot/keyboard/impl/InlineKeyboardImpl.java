package ru.telegrambot.keyboard.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.telegrambot.constant.CallbackDataParts;
import ru.telegrambot.constant.state.PromptState;
import ru.telegrambot.entity.User;
import ru.telegrambot.exeption.BotException;
import ru.telegrambot.keyboard.InlineKeyboard;
import ru.telegrambot.service.UserService;

import java.util.ArrayList;
import java.util.List;

/**
 * Клавиатуры, формируемые в ленте Telegram
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class InlineKeyboardImpl implements InlineKeyboard {

    private final UserService userService;

    // Для админа поиск заданий по именам сотрудника
    @Override
    public InlineKeyboardMarkup getInlineMessageKeyboardWithNames() {
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

        Iterable<User> users = userService.getAllUsers();

        users.forEach(user -> rowList.add(getButton(
                (user.getFirstName() != null && user.getSecondName() != null) ? user.getFirstName() + " " + user.getSecondName() : user.getUserName(),
                CallbackDataParts.USER_NAME_.name() + user.getUserName())));


        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }

    // Просмотр всех задач
    @Override
    public InlineKeyboardMarkup getInlineMessageKeyboardWithPrompts(String name) {
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

        try {
            User user = userService.getUserByUserName(name);
            if (user.getPrompts() != null) {
                user.getPrompts()
                        .stream()
                        .filter(prompt -> prompt.getPromptStateType().equals(PromptState.ACTUAL.name()))
                        .forEach(prompt -> rowList.add(getButton(prompt.getTaskDescription(),
                                CallbackDataParts.PROMPT_.name() + prompt.getTaskDescription()
                        )));
            }

        } catch (BotException e) {
            log.error("Fail to create InlineMessageKeyboardWithPrompts", e);
        }

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }

    private List<InlineKeyboardButton> getButton(String buttonName, String buttonCallBackData) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(buttonName);
        button.setCallbackData(buttonCallBackData);

        List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();
        keyboardButtonsRow.add(button);
        return keyboardButtonsRow;
    }

}
