package ru.telegrambot.keyboard.impl;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.telegrambot.constant.button.PromptKeyboardButtons;
import ru.telegrambot.constant.button.UserKeyboardButtons;
import ru.telegrambot.keyboard.ReplyKeyboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static ru.telegrambot.constant.button.PromptKeyboardButtons.ADD_PROMPT;
import static ru.telegrambot.constant.button.PromptKeyboardButtons.ALL_PROMPTS;
import static ru.telegrambot.constant.button.PromptKeyboardButtons.DELETE_PROMPT;
import static ru.telegrambot.constant.button.PromptKeyboardButtons.FIXED_DATE;
import static ru.telegrambot.constant.button.PromptKeyboardButtons.NO_REPEAT;
import static ru.telegrambot.constant.button.PromptKeyboardButtons.ONCE_A_MONTH;
import static ru.telegrambot.constant.button.PromptKeyboardButtons.ONCE_A_WEEK;
import static ru.telegrambot.constant.button.PromptKeyboardButtons.ONCE_A_YEAR;
import static ru.telegrambot.constant.button.PromptKeyboardButtons.UPDATE_DATE;
import static ru.telegrambot.constant.button.PromptKeyboardButtons.UPDATE_DESCRIPTION;
import static ru.telegrambot.constant.button.PromptKeyboardButtons.UPDATE_PROMPT;
import static ru.telegrambot.constant.button.PromptKeyboardButtons.UPDATE_REMINDING_FREQUENCY;
import static ru.telegrambot.constant.button.PromptKeyboardButtons.USER_MENU;

/**
 * Основная клавиатура, расположенная под строкой ввода текста в Telegram
 */
@Component
public class ReplyKeyboardImpl implements ReplyKeyboard {

    @Override
    public ReplyKeyboardMarkup getDeletionConfirmationKeyboard() {

        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton("✅"));
        row1.add(new KeyboardButton("❎"));

        List<KeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(row1);

        final ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setKeyboard(keyboard);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        return replyKeyboardMarkup;
    }

    @Override
    public ReplyKeyboardMarkup getUserMenuKeyboard() {

        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton(PromptKeyboardButtons.ALL_PROMPTS.getDescription()));

        List<KeyboardRow> keyboard = Collections.singletonList(row1);

        final ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setKeyboard(keyboard);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        return replyKeyboardMarkup;
    }

    @Override
    public ReplyKeyboardMarkup getAdminUserMenuKeyboard() {

        List<KeyboardRow> keyboard = new ArrayList<>();

        for (UserKeyboardButtons userButton : UserKeyboardButtons.values()) {

            KeyboardRow row = new KeyboardRow();
            row.add(new KeyboardButton(userButton.getDescription()));
            keyboard.add(row);
        }

        final ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();

        replyKeyboardMarkup.setKeyboard(keyboard);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        return replyKeyboardMarkup;
    }

    @Override
    public ReplyKeyboardMarkup getAdminPromptMenuKeyboard() {

        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton(ADD_PROMPT.getDescription()));
        row1.add(new KeyboardButton(ALL_PROMPTS.getDescription()));

        KeyboardRow row2 = new KeyboardRow();
        row2.add(new KeyboardButton(DELETE_PROMPT.getDescription()));
        row2.add(new KeyboardButton(UPDATE_PROMPT.getDescription()));

        KeyboardRow row3 = new KeyboardRow();
        row3.add(new KeyboardButton(USER_MENU.getDescription()));

        List<KeyboardRow> keyboard = Arrays.asList(row1, row2, row3);

        final ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();

        replyKeyboardMarkup.setKeyboard(keyboard);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        return replyKeyboardMarkup;
    }

    @Override
    public ReplyKeyboardMarkup getUpdatePromptKeyboard() {
        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton(UPDATE_DESCRIPTION.getDescription()));

        KeyboardRow row2 = new KeyboardRow();
        row2.add(new KeyboardButton(UPDATE_DATE.getDescription()));

        KeyboardRow row3 = new KeyboardRow();
        row3.add(new KeyboardButton(UPDATE_REMINDING_FREQUENCY.getDescription()));

        KeyboardRow row4 = new KeyboardRow();
        row4.add(new KeyboardButton(USER_MENU.getDescription()));

        List<KeyboardRow> keyboard = Arrays.asList(row1, row2, row3, row4);

        final ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setKeyboard(keyboard);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        return replyKeyboardMarkup;
    }

    @Override
    public ReplyKeyboardMarkup getRemindingKeyboard() {

        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton(FIXED_DATE.getDescription()));

        KeyboardRow row2 = new KeyboardRow();
        row2.add(new KeyboardButton(ONCE_A_WEEK.getDescription()));

        KeyboardRow row3 = new KeyboardRow();
        row3.add(new KeyboardButton(ONCE_A_MONTH.getDescription()));

        KeyboardRow row4 = new KeyboardRow();
        row4.add(new KeyboardButton(ONCE_A_YEAR.getDescription()));

        KeyboardRow row5 = new KeyboardRow();
        row5.add(new KeyboardButton(NO_REPEAT.getDescription()));

        List<KeyboardRow> keyboard = Arrays.asList(row1, row2, row3, row4, row5);

        final ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();

        replyKeyboardMarkup.setKeyboard(keyboard);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        return replyKeyboardMarkup;
    }

}
