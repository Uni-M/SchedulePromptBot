package ru.telegrambot.service;

import ru.telegrambot.entity.User;
import ru.telegrambot.exeption.BotException;

public interface UserService {

    void updateUserInfo(User user);

    void deleteUser(String name);

    Iterable<User> getAllUsers();

    void saveUserChatId(String userName, Long chatId);

    void saveUserNames(String[] names);

    void updateUserNames(String userName, String[] names) throws BotException ;

    User getUserByUserName(String userName) throws BotException;

    Long getChatId(String userName);

    void saveTimeZone(String userName, String  timeZone);

    String getTimeZone(String userName);
}
