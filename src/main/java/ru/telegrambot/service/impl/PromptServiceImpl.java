package ru.telegrambot.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Service;
import ru.telegrambot.constant.message.BotExceptionMessage;
import ru.telegrambot.constant.message.BotMessageTemplate;
import ru.telegrambot.constant.state.PromptState;
import ru.telegrambot.entity.Prompt;
import ru.telegrambot.repository.PromptRepository;
import ru.telegrambot.service.PromptService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.TimeZone;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class PromptServiceImpl implements PromptService {

    private final PromptRepository promptRepository;

    @Override
    public Prompt getByState(PromptState state) {

        Optional<Prompt> entity = promptRepository.getByPromptStateType(state.name());

        try {
            return entity.get();
        } catch (NullPointerException | NoSuchElementException e) {
            log.error(BotExceptionMessage.FAIL_FIND_PROMPT_WITH_STATE.getMessage(), state.name(), e);
        } catch (IncorrectResultSizeDataAccessException e) {
            log.error(BotExceptionMessage.FAIL_GET_PROMPT_WITH_STATE.getMessage(), state.name(), e);
        }
        return null;
    }

    @Override
    public void savePrompt(Prompt prompt) {
        promptRepository.save(prompt);
    }

    @Override
    public void deletePrompt(Prompt prompt) {
        promptRepository.delete(prompt);
    }

    @Override
    public void updatePrompt(String name, Instant newDate) {
        promptRepository.updateDate(name, newDate);
    }

    @Override
    public Optional<List<Prompt>> getActualPrompts(Instant start, Instant end) {
        return promptRepository.findAllByDateBetween(start, end);
    }

    @Override
    public boolean saveDate(String newDate, PromptState newState) { //, String timeZone

        Optional<Prompt> entity = promptRepository.getByPromptStateType(PromptState.SET_PROMPT_DATE.name());
        try {
            Prompt prompt = entity.get();

            SimpleDateFormat formatInp = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            // TODO добавить учет часового пояса
            formatInp.setTimeZone(TimeZone.getTimeZone("GMT+01:00"));
            Instant dateInstant = formatInp.parse(newDate).toInstant();

            prompt.setDate(dateInstant);
            prompt.setPromptStateType(newState.name());
            promptRepository.save(prompt);
            return true;

        } catch (NullPointerException | NoSuchElementException e) {
            log.error(BotExceptionMessage.FAIL_FIND_PROMPT_WITH_STATE.getMessage(), PromptState.SET_PROMPT_DATE.name(), e);
        } catch (IncorrectResultSizeDataAccessException e) {
            log.error(BotExceptionMessage.FAIL_GET_PROMPT_WITH_STATE.getMessage(), PromptState.SET_PROMPT_DATE.name(), e);
        } catch (ParseException e) {
            log.error("Failed to save date. Invalid input: {}", newDate, e);
        }

        return false;

    }

    @Override
    public void updateState(String name, PromptState stateType) {
        promptRepository.updateChatState(name, stateType.name());
    }

    @Override
    public String updateState(PromptState oldState, PromptState newState) {

        Optional<Prompt> entity = promptRepository.getByPromptStateType(oldState.name());

        try {
            Prompt prompt = entity.get();
            prompt.setPromptStateType(newState.name());
            promptRepository.save(prompt);

            log.info(BotExceptionMessage.SUCCESS_ADD_PROMPT_STATE.getMessage(),
                    PromptState.SET_PROMPT_DATE.name());
            return BotMessageTemplate.UPDATE_PROMPT_DATE_MESSAGE.getDescription();

        } catch (NullPointerException | NoSuchElementException e) {
            log.error(BotExceptionMessage.FAIL_FIND_PROMPT_WITH_STATE.getMessage(),
                    PromptState.SET_PROMPT_DATE.name(), e);
            return BotMessageTemplate.ERROR_STATE_MESSAGE.getDescription();
        }

    }

}