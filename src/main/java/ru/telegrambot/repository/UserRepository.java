package ru.telegrambot.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.telegrambot.entity.User;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, String> {

    @Transactional
    void deleteUserByUserName(String userName);
    Optional<User> getByUserName(String userName);

    @Transactional
    @Query(value = "SELECT chat_id " +
            "FROM public.users " +
            "WHERE user_name = :userName",
            nativeQuery = true)
    Optional<Long> getChatIdByUserName(@Param("userName") String userName);

    @Modifying
    @Transactional
    @Query(value = "UPDATE public.users " +
            "SET chat_id = :chatId " +
            "WHERE user_name = :userName",
            nativeQuery = true)
    void saveChatId(@Param("userName") String userName,
                    @Param("chatId") Long chatId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE public.users " +
            "SET time_zone = :timeZone " +
            "WHERE user_name = :userName",
            nativeQuery = true)
    void saveLocale(@Param("userName") String userName,
                    @Param("timeZone") String timeZone);


    @Transactional
    @Query(value = "SELECT time_zone " +
            "FROM public.users " +
            "WHERE user_name = :userName",
            nativeQuery = true)
    Optional<String> getLocale(@Param("userName") String userName);

}
