package ru.telegrambot.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.telegrambot.entity.User;
import ru.telegrambot.exeption.BotException;
import ru.telegrambot.repository.UserRepository;
import ru.telegrambot.service.UserService;
import ru.telegrambot.utils.EnvHelper;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.TimeZone;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public void saveUserNames(String[] names) {

        User user = new User();
        user.setUserName(names[0]);
        user.setFirstName(names[1]);
        user.setSecondName(names[2]);
        user.setTimeZone(userRepository.getLocale(EnvHelper.getValue("ADMIN_NAME")).orElse("+00:00"));
        userRepository.save(user);

    }

    @Override
    public void updateUserNames(String userName, String[] names) throws BotException {

        Optional<User> entity = userRepository.getByUserName(userName);

        try {
            User user = entity.get();
            user.setFirstName(names[0]);
            user.setSecondName(names[1]);
            userRepository.save(user);

        } catch (NullPointerException | NoSuchElementException e) {
            throw new BotException("Fail to find user with username: " + userName, e);
        }

    }

    @Override
    public User getUserByUserName(String userName) throws BotException {
        Optional<User> entity = userRepository.getByUserName(userName);

        try {
            return entity.get();
        } catch (NullPointerException | NoSuchElementException e) {
            throw new BotException("Fail to find user with username: " + userName, e);
        }
    }

    @Override
    public Long getChatId(String userName) {
        Optional<Long> chatId = userRepository.getChatIdByUserName(userName);

        try {
            return chatId.get();
        } catch (NullPointerException | NoSuchElementException e) {
            log.error("Fail to get chat id for user with username: {}", userName, e);
        }

        return null;

    }

    @Override
    public void saveTimeZone(String userName, String timeZone) throws BotException {

        try {
            userRepository.saveLocale(userName, timeZone);
        } catch (NullPointerException | NoSuchElementException e) {
            throw new BotException("Fail to find user with username: " + userName, e);
        }

    }

    @Override
    public String getTimeZone(String userName) throws BotException {

        try {
            return userRepository.getLocale(userName).orElse("+00:00");
        } catch (NullPointerException | NoSuchElementException e) {
            throw new BotException("Fail to find user with username: " + userName, e);
        }
    }

    @Override
    public void updateUserInfo(User user) {
        userRepository.save(user);
    }

    @Override
    public void deleteUser(String userName) throws BotException {

        try {
            userRepository.deleteUserByUserName(userName);
        } catch (NullPointerException | NoSuchElementException e) {
            throw new BotException("Fail to find user with username: " + userName, e);
        }

    }

    @Override
    public Iterable<User> getAllUsers() throws BotException {
        if (userRepository.count() != 0) {
            return userRepository.findAll();
        } else {
            throw new BotException("Fail get list of users. At least one user (admin) must be in the database");
        }

    }

    @Override
    public void saveUserChatId(String userName, Long chatId) throws BotException {

        try {
            userRepository.saveChatId(userName, chatId);
        } catch (NullPointerException | NoSuchElementException e) {
            throw new BotException("Fail to find user with username: " + userName, e);
        }
    }


}
