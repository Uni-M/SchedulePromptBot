package ru.telegrambot.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Service;
import ru.telegrambot.constant.message.BotMessageTemplate;
import ru.telegrambot.constant.state.PromptState;
import ru.telegrambot.entity.Prompt;
import ru.telegrambot.repository.PromptRepository;
import ru.telegrambot.service.PromptService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
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
            log.error("Fail to find prompt with state: {}", state.name(), e);
        } catch (IncorrectResultSizeDataAccessException e) {
            log.error("Fail to get prompt with state: {}. Must be only one prompt (excl. ACTUAL and NOT_ACTUAL), bur was found more",
                    state.name(), e);
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
    public Optional<List<Prompt>> getActualPrompts(Instant start, Instant end) {
        return promptRepository.findAllByDateBetween(start, end);
    }

    @Override
    public boolean saveDate(String newDate, PromptState newState) {

        try {
            SimpleDateFormat formatInp = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            String tz = promptRepository.getTimeZoneWithState(PromptState.SET_PROMPT_DATE.name()).get();

            formatInp.setTimeZone(TimeZone.getTimeZone(tz));
            Instant dateInstant = formatInp.parse(newDate).toInstant();

            ZonedDateTime zdt = LocalDateTime.ofInstant(dateInstant, ZoneId.ofOffset("GMT", ZoneOffset.of(tz))).atZone(ZoneId.ofOffset("GMT", ZoneOffset.of(tz)));
            LocalDateTime localDateTimeWTZ = zdt.toLocalDateTime();

            promptRepository.updateDate(localDateTimeWTZ, newState.name(), PromptState.SET_PROMPT_DATE.name());
            return true;

        } catch (NullPointerException | NoSuchElementException e) {
            log.error("Fail to find prompt with state: {}", PromptState.SET_PROMPT_DATE.name(), e);
        } catch (IncorrectResultSizeDataAccessException e) {
            log.error("Fail to get prompt with state: {}. Must be only one prompt (excl. ACTUAL and NOT_ACTUAL), bur was found more",
                    PromptState.SET_PROMPT_DATE.name(), e);
        } catch (ParseException e) {
            log.error("Fail to save date. Invalid input: {}", newDate, e);
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

            log.info("Prompt state add successful. New state: {}",
                    PromptState.SET_PROMPT_DATE.name());
            return BotMessageTemplate.UPDATE_PROMPT_DATE_MESSAGE.getDescription();

        } catch (NullPointerException | NoSuchElementException e) {
            log.error("Fail to find prompt with state: {}",
                    PromptState.SET_PROMPT_DATE.name(), e);
            return BotMessageTemplate.ERROR_STATE_MESSAGE.getDescription();
        }

    }

}