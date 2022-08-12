package ru.telegrambot.constant.state;

/**
 * Состояние чата
 */
public enum ChatStateType {

    SAVE_LOCALE,
    CHOOSE_ACTION,
    ACTUAL_STATE,

    ADD_USER,
    UPDATE_USER,
    DELETE_USER,
    GET_ALL_USERS,

    ADD_PROMPT,
    UPDATE_PROMPT,
    UPDATE_PROMPT_DATE,
    SET_PROMPT_DATE,
    SET_REMINDER_FREQUENCY,
    DELETE_PROMPT,
    GET_ALL_PROMPTS

}
