package ru.telegrambot.service;

import ru.telegrambot.constant.state.ChatStateType;
import ru.telegrambot.entity.ChatState;

public interface StateControlService {

    void addState (String key, ChatStateType stateType);

    ChatState getState();

    void updateStateForLastUser(ChatStateType stateType);

    boolean checkState(ChatStateType stateType);

}
