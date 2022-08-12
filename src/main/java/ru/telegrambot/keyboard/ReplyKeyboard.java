package ru.telegrambot.keyboard;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

public interface ReplyKeyboard {

    ReplyKeyboardMarkup getDeletionConfirmationKeyboard();

    ReplyKeyboardMarkup getUserMenuKeyboard();

    ReplyKeyboardMarkup getAdminUserMenuKeyboard();

    ReplyKeyboardMarkup getAdminPromptMenuKeyboard();

    ReplyKeyboardMarkup getUpdatePromptKeyboard();

    ReplyKeyboardMarkup getRemindingKeyboard();
}
