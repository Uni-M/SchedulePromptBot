package ru.telegrambot;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import ru.telegrambot.config.PostgreConfig;
import ru.telegrambot.entity.User;
import ru.telegrambot.service.UserService;
import ru.telegrambot.service.impl.UserServiceImpl;

import static org.mockito.ArgumentMatchers.any;

@Slf4j
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = PostgreConfig.class)
class UserServiceTest {

    static UserService userServiceMock = Mockito.mock(UserServiceImpl.class);

    @Test
    void saveUserNamesTest() {
        Mockito.doAnswer(invocation -> {
            User user = userServiceMock.getUserByUserName(invocation.getArgument(0));

            Assertions.assertEquals(user.getFirstName(), "Jones");
            Assertions.assertEquals(user.getSecondName(), "Cat");

            return null;
        }).when(userServiceMock).saveUserNames(new String[]{any(String.class)});

        userServiceMock.saveUserNames(new String[]{"Jonsey", "Jones", "Cat"});
    }

    @Test
    void updateUserNamesTest() {
        Mockito.doAnswer(invocation -> {
            userServiceMock.updateUserNames(invocation.getArgument(0), new String[]{"AnnaBanana", "Anna", "Banana"});
            User user = userServiceMock.getUserByUserName(invocation.getArgument(0));

            Assertions.assertEquals(user.getFirstName(), "Ellen");
            Assertions.assertEquals(user.getSecondName(), "Ripley");

            return null;
        }).when(userServiceMock).saveUserNames(new String[]{any(String.class)});

        userServiceMock.saveUserNames(new String[]{"Aliens Queen", "Ellen", "Ripley"});
    }

    @Test
    void saveTimeZoneTest() {

    }

    @Test
    void updateUserInfoTest() {

    }

    @Test
    void deleteUserTest() {

    }

    @Test
    void getAllUsersTest() {

    }

    @Test
    void saveAndGetUserChatIdTest() {

    }

}
