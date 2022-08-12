package ru.telegrambot.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.telegrambot.constant.state.ChatStateType;
import ru.telegrambot.entity.ChatState;
import ru.telegrambot.repository.ChatStateRepository;
import ru.telegrambot.service.StateControlService;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class StateControlServiceImpl implements StateControlService {

    private final ChatStateRepository stateRepository;
    private final Long DEFAULT_ID = 1L;

    @Override
    public void addState (String userName, ChatStateType state) {

        ChatState chatState;

        if (stateRepository.count() == 0) {
            chatState = new ChatState();
            chatState.setId(DEFAULT_ID);
        } else {
            chatState = stateRepository.getById(DEFAULT_ID);

        }

        chatState.setChatState(state.name());
        chatState.setUserName(userName);
        stateRepository.save(chatState);
    }

    @Override
    public ChatState getState() {
        return stateRepository.getById(DEFAULT_ID);
    }

    @Override
    public void updateStateForLastUser(ChatStateType stateType) {

        ChatState chatState = stateRepository.getById(DEFAULT_ID);
        chatState.setChatState(stateType.name());
        stateRepository.save(chatState);
    }

    @Override
    public boolean checkState(ChatStateType state) {
        return stateRepository.existsByChatState(state.name());
    }


//
//private Map<String, ChatState> stateMap = new HashMap<>();
//
//    @Override
//    public void addState (String key, ChatState state) {
//        stateMap.put(key, state);
//    }
//
//    @Override
//    public ChatState getStateByKey(String key) {
//        return stateMap.get(key);
//    }
//
//    @Override
//    public boolean checkState(ChatState state) {
//        return stateMap.entrySet()
//                .stream()
//                .filter(entry -> state == entry.getValue())
//                .map(Map.Entry::getKey)
//                .findAny()
//                .isPresent();
//    }
//
//    @Override
//    public void deleteState (String key) {
//        stateMap.remove(key);
//    }
//
//    @Override
//    public String getByState(ChatState state) {
//        return stateMap.entrySet()
//                .stream()
//                .filter(entry -> state == entry.getValue())
//                .map(Map.Entry::getKey)
//                .findFirst()
//                .orElseThrow(() -> new IllegalStateException(
//                        "No User for state \"" + state.name() + "\""));
//    }
//        @Override
//    public List<String> getByState(ChatState state) {
//        return stateMap.entrySet()
//                .stream()
//                .filter(entry -> state == entry.getValue())
//                .map(Map.Entry::getKey)
//                .collect(Collectors.toList());
//    }

}
