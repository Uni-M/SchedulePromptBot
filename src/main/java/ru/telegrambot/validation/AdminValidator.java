package ru.telegrambot.validation;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Service
public class AdminValidator implements Validator {

    @Value("${telegram.admin.user-name}")
    private static String ADMIN_USERNAME;

    @Override
    public boolean supports(Class<?> aClass) {
        return String.class.equals(aClass);
    }

    @Override
    public void validate(Object obj, Errors errors) {

        if (!obj.equals(ADMIN_USERNAME)) {
            errors.rejectValue(null, "User isn`t admin");
        }
    }
}
