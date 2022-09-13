package ru.telegrambot.service;

import ru.telegrambot.constant.state.PromptState;
import ru.telegrambot.entity.Prompt;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PromptService {

    Prompt getByState(PromptState stateType);

    void savePrompt(Prompt prompt);


    void deletePrompt(Prompt prompt);

    Optional<List<Prompt>> getActualPrompts(Instant start, Instant end);

    boolean saveDate(String newDate, PromptState newState);

    void updateState (String name, PromptState stateType);

    String updateState(PromptState oldState, PromptState newState);

    void updatePrompt(String name, LocalDateTime newDate);

    List<Prompt> getAllByState(PromptState state);
}
