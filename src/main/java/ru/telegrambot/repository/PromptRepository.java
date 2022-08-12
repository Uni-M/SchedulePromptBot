package ru.telegrambot.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.telegrambot.entity.Prompt;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface PromptRepository extends CrudRepository<Prompt, String> {

    Optional<List<Prompt>> findAllByDateBetween(Instant startInstant, Instant endInstant);
    Optional<Prompt> findByTaskDescription(String name);

    boolean existsByPromptStateType(String state);
    Optional<Prompt> getByPromptStateType(String state);
    Optional<Prompt> getByTaskDescription(String name);
    Optional<List<Prompt>> getByUserName(String name);

    @Modifying
    @Transactional
    @Query(value = "UPDATE public.prompts " +
            "SET reminding_date = :newDate " +
            "WHERE task_description = :name",
            nativeQuery = true)
    void updateDate(@Param("name") String name,
                    @Param("newDate") Instant newDate);

    @Modifying
    @Transactional
    @Query(value = "UPDATE public.prompts " +
            "SET state = :state " +
            "WHERE task_description = :description",
            nativeQuery = true)
    void updateChatState(@Param("description") String description,
                         @Param("state") String state);

}
