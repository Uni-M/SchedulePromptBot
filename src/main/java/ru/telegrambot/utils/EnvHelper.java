package ru.telegrambot.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class EnvHelper {

    /**
     * Получить свойство среды, если оно не существует - попробовать получить системное свойство.
     * @param name имя свойства
     * @return значение свойства или null
     */
    public static String getValue(String name) {
        String value = System.getenv(name);
        if (value == null) {
            value = System.getProperty(name);
        }

        return value;
    }

}
