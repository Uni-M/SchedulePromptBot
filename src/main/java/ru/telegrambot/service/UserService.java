package ru.telegrambot.service;

import ru.telegrambot.entity.User;
import ru.telegrambot.exeption.BotException;

public interface UserService {

    void updateUserInfo(User user);

    void deleteUser(String name) throws BotException;

    Iterable<User> getAllUsers() throws BotException;

    void saveUserChatId(String userName, Long chatId) throws BotException;

    void saveUserNames(String[] names);

    void updateUserNames(String userName, String[] names) throws BotException ;

    User getUserByUserName(String userName) throws BotException;

    Long getChatId(String userName);

    void saveTimeZone(String userName, String  timeZone) throws BotException;

    String getTimeZone(String userName) throws BotException;
}
