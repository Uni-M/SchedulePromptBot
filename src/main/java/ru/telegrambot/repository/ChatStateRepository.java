package ru.telegrambot.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.telegrambot.entity.ChatState;

@Repository
public interface ChatStateRepository extends CrudRepository<ChatState, String> {

    boolean existsByChatState(String state);
    ChatState getById(Long id);

}
