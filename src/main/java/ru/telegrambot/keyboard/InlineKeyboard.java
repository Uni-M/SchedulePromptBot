package ru.telegrambot.keyboard;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.telegrambot.exeption.BotException;

public interface InlineKeyboard {

    InlineKeyboardMarkup getInlineMessageKeyboardWithNames() throws BotException;

    InlineKeyboardMarkup getInlineMessageKeyboardWithPrompts(String name);

}
