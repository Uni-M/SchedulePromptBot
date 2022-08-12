package ru.telegrambot.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.telegrambot.constant.message.BotExceptionMessage;
import ru.telegrambot.entity.User;
import ru.telegrambot.exeption.BotException;
import ru.telegrambot.repository.UserRepository;
import ru.telegrambot.service.UserService;

import java.util.NoSuchElementException;
import java.util.Optional;

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
            throw new BotException(BotExceptionMessage.FAIL_FIND_USER.getMessage() + userName, e);
        }

    }

    @Override
    public User getUserByUserName(String userName) throws BotException {
        Optional<User> entity = userRepository.getByUserName(userName);

        try {
            return entity.get();
        } catch (NullPointerException | NoSuchElementException e) {
            throw new BotException(BotExceptionMessage.FAIL_FIND_USER.getMessage() + userName, e);
        }
    }

    @Override
    public Long getChatId(String userName) {
        Optional<Long> chatId = userRepository.getChatIdByUserName(userName);

        try {
            return chatId.get();
        } catch (NullPointerException | NoSuchElementException e) {
            log.error(BotExceptionMessage.FAIL_GET_CHAT_ID.getMessage(), userName, e);
        }

        return null;

    }

    @Override
    public void saveTimeZone(String userName, String timeZone) {
        userRepository.saveLocale(userName, timeZone);
    }

    @Override
    public String getTimeZone(String userName) {
        return userRepository.getLocale(userName).orElse("+00:00");
    }

    @Override
    public void updateUserInfo(User user) {
        userRepository.save(user);
    }

    @Override
    public void deleteUser(String name) {
        userRepository.deleteUserByUserName(name);
    }

    @Override
    public Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public void saveUserChatId(String userName, Long chatId) {
        userRepository.saveChatId(userName, chatId);
    }


}
