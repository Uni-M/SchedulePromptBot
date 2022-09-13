package ru.telegrambot.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.telegrambot.entity.Prompt;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PromptRepository extends CrudRepository<Prompt, String> {

    Optional<List<Prompt>> findAllByDateBetween(Instant startInstant, Instant endInstant);
    Optional<Prompt> getByPromptStateType(String state);
    Optional<List<Prompt>> findByPromptStateType(String state);

    @Transactional
    @Query(value = "SELECT users.time_zone " +
            "FROM users " +
            "JOIN prompts " +
            "ON users.user_name = prompts.user_name " +
            "WHERE prompts.user_name = :name " +
            "LIMIT 1",
            nativeQuery = true)
    Optional<String> getTimeZone(@Param("name") String name);


    @Transactional
    @Query(value = "SELECT users.time_zone " +
            "FROM users " +
            "JOIN prompts " +
            "ON users.user_name = prompts.user_name " +
            "WHERE prompts.state = :state " +
            "LIMIT 1",
            nativeQuery = true)
    Optional<String> getTimeZoneWithState(@Param("state") String state);

    @Modifying
    @Transactional
    @Query(value = "UPDATE public.prompts " +
            "SET reminding_date = :newDate, state = :newState " +
            "WHERE state = :oldState",
            nativeQuery = true)
    void updateDate(@Param("newDate") LocalDateTime newDate,
                    @Param("newState") String newState,
                    @Param("oldState") String oldState);

    @Modifying
    @Transactional
    @Query(value = "UPDATE public.prompts " +
            "SET reminding_date = :newDate " +
            "WHERE task_description = :description",
            nativeQuery = true)
    void updateDate(@Param("newDate") String description,
                    @Param("newDate") LocalDateTime newDate);

    @Modifying
    @Transactional
    @Query(value = "UPDATE public.prompts " +
            "SET state = :state " +
            "WHERE task_description = :description",
            nativeQuery = true)
    void updateChatState(@Param("description") String description,
                         @Param("state") String state);


}
