package ru.telegrambot.keyboard;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

public interface InlineKeyboard {

    InlineKeyboardMarkup getInlineMessageKeyboardWithNames();

    InlineKeyboardMarkup getInlineMessageKeyboardWithPrompts(String name);

}
