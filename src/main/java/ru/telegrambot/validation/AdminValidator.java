package ru.telegrambot.validation;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.telegrambot.utils.EnvHelper;

@Service
public class AdminValidator implements Validator {

    private static final String ADMIN_USERNAME = EnvHelper.getValue("ADMIN_NAME");

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
