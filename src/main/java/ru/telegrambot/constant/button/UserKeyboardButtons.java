package ru.telegrambot.constant.button;

/**
 * Названия кнопок с пользователями основной клавиатуры
 */
public enum UserKeyboardButtons {

    ALL_USERS("Показать всех пользователей"),
    ADD_USER("Добавить пользователя"),
    DELETE_USER("Удалить пользователя"),
    UPDATE_USER("Обновить И.Ф. пользователя");

    private final String buttonDescription;

    UserKeyboardButtons(String buttonDescription) {
        this.buttonDescription = buttonDescription;
    }

    public String getDescription() {
        return buttonDescription;
    }
}
