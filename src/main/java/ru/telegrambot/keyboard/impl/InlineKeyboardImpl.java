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
import ru.telegrambot.service.PromptService;
import ru.telegrambot.service.UserService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static ru.telegrambot.constant.button.PromptKeyboardButtons.ADD_PROMPT;
import static ru.telegrambot.constant.button.PromptKeyboardButtons.ALL_PROMPTS;
import static ru.telegrambot.constant.button.PromptKeyboardButtons.CANCEL;
import static ru.telegrambot.constant.button.PromptKeyboardButtons.CHOOSE;
import static ru.telegrambot.constant.button.PromptKeyboardButtons.CONFIRM;
import static ru.telegrambot.constant.button.PromptKeyboardButtons.DELETE_PROMPT;
import static ru.telegrambot.constant.button.PromptKeyboardButtons.NEXT;
import static ru.telegrambot.constant.button.PromptKeyboardButtons.PREVIOUS;
import static ru.telegrambot.constant.button.PromptKeyboardButtons.UPDATE_DATE;
import static ru.telegrambot.constant.button.PromptKeyboardButtons.UPDATE_DESCRIPTION;
import static ru.telegrambot.constant.button.PromptKeyboardButtons.UPDATE_PROMPT;
import static ru.telegrambot.constant.button.PromptKeyboardButtons.UPDATE_REMINDING_FREQUENCY;
import static ru.telegrambot.constant.button.PromptKeyboardButtons.USER_MENU;

/**
 * Клавиатуры, формируемые в ленте Telegram
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class InlineKeyboardImpl implements InlineKeyboard {

    private final UserService userService;
    private final PromptService promptService;

    // Для админа поиск заданий по именам сотрудника
    @Override
    public InlineKeyboardMarkup getInlineMessageKeyboardWithNames() throws BotException {

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

        Iterable<User> users = userService.getAllUsers();

        users.forEach(user -> rowList.add(Collections.singletonList(InlineKeyboardButton.builder()
                .text((user.getFirstName() != null && user.getSecondName() != null) ? user.getFirstName() + " " + user.getSecondName() : user.getUserName())
                .callbackData(CallbackDataParts.USER_NAME_.name() + user.getUserName())
                .build())));

        return InlineKeyboardMarkup.builder()
                .keyboard(rowList)
                .build();
    }

    // Просмотр всех задач
    @Override
    public InlineKeyboardMarkup getInlineMessageKeyboardWithPrompts(String name, Long page) {

        List<InlineKeyboardButton> paginationButtons = new ArrayList<>();
        Long MAX_PAGE;

        MAX_PAGE = promptService.getAllByState(PromptState.ACTUAL).stream().count();
        System.out.println(page + " " + MAX_PAGE); // TODO удалить
        if (MAX_PAGE.equals(1L)) {
            paginationButtons.add(
                    InlineKeyboardButton.builder()
                            .text(CHOOSE.getDescription())
                            .callbackData(CallbackDataParts.ACTION_.name() + CHOOSE.name())
                            .build());


        } else if (page == 0) {
            paginationButtons.add(
                    InlineKeyboardButton.builder()
                            .text(CHOOSE.getDescription())
                            .callbackData(CallbackDataParts.ACTION_.name() + CHOOSE.name())
                            .build());
            paginationButtons.add(
                    InlineKeyboardButton.builder()
                            .text(NEXT.getDescription())
                            .callbackData(CallbackDataParts.ACTION_.name() + NEXT.name())
                            .build());

        } else if (page.equals(MAX_PAGE - 1)) {
            paginationButtons.add(
                    InlineKeyboardButton.builder()
                            .text(PREVIOUS.getDescription())
                            .callbackData(CallbackDataParts.ACTION_.name() + PREVIOUS.name())
                            .build());
            paginationButtons.add(
                    InlineKeyboardButton.builder()
                            .text(CHOOSE.getDescription())
                            .callbackData(CallbackDataParts.ACTION_.name() + CHOOSE.name())
                            .build());

        } else {
            paginationButtons.add(
                    InlineKeyboardButton.builder()
                            .text(PREVIOUS.getDescription())
                            .callbackData(CallbackDataParts.ACTION_.name() + PREVIOUS.name())
                            .build());
            paginationButtons.add(
                    InlineKeyboardButton.builder()
                            .text(CHOOSE.getDescription())
                            .callbackData(CallbackDataParts.ACTION_.name() + CHOOSE.name())
                            .build());
            paginationButtons.add(
                    InlineKeyboardButton.builder()
                            .text(NEXT.getDescription())
                            .callbackData(CallbackDataParts.ACTION_.name() + NEXT.name())
                            .build());
        }

        return InlineKeyboardMarkup.builder()
                .keyboardRow(paginationButtons)
                .keyboardRow(Collections.singletonList(
                        InlineKeyboardButton.builder()
                                .text(ADD_PROMPT.getDescription())
                                .callbackData(CallbackDataParts.ACTION_.name() + ADD_PROMPT.name())
                                .build()))
                .build();

    }

    // Клавиатура для админа с возможными действиями с напоминаниями
    @Override
    public InlineKeyboardMarkup getInlineMessageKeyboardWithActions(boolean isAdmin) {

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

        if (isAdmin) {
            rowList.add(Collections.singletonList(
                    InlineKeyboardButton.builder()
                            .text(DELETE_PROMPT.getDescription())
                            .callbackData(CallbackDataParts.ACTION_.name() + DELETE_PROMPT.name())
                            .build()));
            rowList.add(Collections.singletonList(
                    InlineKeyboardButton.builder()
                            .text(UPDATE_PROMPT.getDescription())
                            .callbackData(CallbackDataParts.ACTION_.name() + UPDATE_PROMPT.name())
                            .build()));
            rowList.add(Collections.singletonList(
                    InlineKeyboardButton.builder()
                            .text(ALL_PROMPTS.getDescription())
                            .callbackData(CallbackDataParts.ACTION_.name() + ALL_PROMPTS.name())
                            .build()));
            rowList.add(Collections.singletonList(
                    InlineKeyboardButton.builder()
                            .text(USER_MENU.getDescription())
                            .callbackData(CallbackDataParts.ACTION_.name() + USER_MENU.name())
                            .build()));
        } else {
            rowList.add(Collections.singletonList(
                    InlineKeyboardButton.builder()
                            .text(USER_MENU.getDescription())
                            .callbackData(CallbackDataParts.ACTION_.name() + USER_MENU.name())
                            .build()));
        }

        return InlineKeyboardMarkup.builder()
                .keyboard(rowList)
                .build();

    }

    // Клавиатура для админа с возможными действиями с напоминаниями
    @Override
    public InlineKeyboardMarkup getInlineMessageKeyboardWithUpdates() {

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

        rowList.add(Collections.singletonList(
                InlineKeyboardButton.builder()
                        .text(UPDATE_DATE.getDescription())
                        .callbackData(CallbackDataParts.PROMPT_.name() + UPDATE_DATE.name())
                        .build()));
        rowList.add(Collections.singletonList(
                InlineKeyboardButton.builder()
                        .text(UPDATE_DESCRIPTION.getDescription())
                        .callbackData(CallbackDataParts.PROMPT_.name() + UPDATE_DESCRIPTION.name())
                        .build()));
        rowList.add(Collections.singletonList(
                InlineKeyboardButton.builder()
                        .text(UPDATE_REMINDING_FREQUENCY.getDescription())
                        .callbackData(CallbackDataParts.PROMPT_.name() + UPDATE_REMINDING_FREQUENCY.name())
                        .build()));

        return InlineKeyboardMarkup.builder()
                .keyboard(rowList)
                .build();

    }

    @Override
    public InlineKeyboardMarkup getInlineMessageKeyboardWithConfirmation() {

        List<InlineKeyboardButton> rowList = new ArrayList<>();

        rowList.add(
                InlineKeyboardButton.builder()
                        .text(CONFIRM.getDescription())
                        .callbackData(CallbackDataParts.ACTION_.name() + CONFIRM.name())
                        .build());
        rowList.add(
                InlineKeyboardButton.builder()
                        .text(CANCEL.getDescription())
                        .callbackData(CallbackDataParts.ACTION_.name() + CANCEL.name())
                        .build());


        return InlineKeyboardMarkup.builder()
                .keyboardRow(rowList)
                .build();
    }

}
