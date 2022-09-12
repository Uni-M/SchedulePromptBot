package ru.telegrambot.constant.button;

/**
 * Названия кнопок с напоминаниями основной клавиатуры
 */
public enum PromptKeyboardButtons {

    ALL_PROMPTS("Получить все напоминания"),
    ADD_PROMPT("Создать напоминание"),
    DELETE_PROMPT("Удалить напоминание"),
    UPDATE_PROMPT("Обновить напоминание"),

    UPDATE_DESCRIPTION("Обновить описание"),
    UPDATE_DATE("Обновить ближайшую дату"),
    UPDATE_REMINDING_FREQUENCY("Обновить частоту напоминания"),

    ONCE_A_WEEK("Раз в неделю"),
    ONCE_A_MONTH("Раз в месяц"),
    ONCE_A_YEAR("Раз в год"),
    NO_REPEAT("Без повторений"),
    FIXED_DATE("Указать ближайшую дату"),

    CONFIRM("✅"),
    CANCEL("❎"),

    CHOOSE("Выбрать"),
    NEXT("▶"),
    PREVIOUS("◀"),

    USER_MENU("Вернуться в основное меню");


    private final String buttonDescription;

    PromptKeyboardButtons(String buttonDescription) {
        this.buttonDescription = buttonDescription;
    }

    public String getDescription() {
        return buttonDescription;
    }
}