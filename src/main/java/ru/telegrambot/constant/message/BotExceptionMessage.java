package ru.telegrambot.constant.message;

public enum BotExceptionMessage {

    SUCCESS_ADD_PROMPT("Prompt info add successful. New info: {}"),
    SUCCESS_ADD_PROMPT_STATE("Prompt state add successful. New state: {}"),

    FAIL_GET_FULL_NAME("Fail to get full name for user with username: {}"),
    FAIL_FIND_USER("Fail to find user with username: {}"),
    FAIL_ADD_PROMPT("Fail to add prompt for user with username: {}"),
    FAIL_FIND_PROMPT_WITH_STATE("Fail to find prompt with state: {}"),
    FAIL_DATE_PARSING("Fail to parse date. Date is missing"),
    FAIL_GET_PROMPT_WITH_STATE("Fail to get prompt with state: {}. Must be only one prompt (excl. ACTUAL and NOT_ACTUAL), bur was found more"),
    FAIL_GET_CHAT_ID("Fail to get chat id for user with username: {}");

    private final String msg;

    BotExceptionMessage(String msg) {
        this.msg = msg;
    }

    public String getMessage() {
        return msg;
    }
}