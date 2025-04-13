package com.sd.tennis.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidatorImpl implements ConstraintValidator<PasswordValidator, String> {
    private static final String SMALL_LETTER = "(.*[a-z].*)";
    private static final String CAPITAL_LETTER = "(.*[A-Z].*)";
    private static final String DIGIT = "(.*[0-9].*)";
    private static final String SPECIAL_CHARACTER = "(.*[@#$%^&+=].*)";
    private static final int MIN_LENGTH = 5;
    private static final int MAX_LENGTH = 20;

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null) {
            return false;
        }

        boolean hasSmallLetter = password.matches(SMALL_LETTER);
        boolean hasCapitalLetter = password.matches(CAPITAL_LETTER);
        boolean hasDigit = password.matches(DIGIT);
        boolean hasSpecialCharacter = password.matches(SPECIAL_CHARACTER);
        boolean isLengthValid = password.length() >= MIN_LENGTH && password.length() <= MAX_LENGTH;

        return hasSmallLetter && hasCapitalLetter && hasDigit && hasSpecialCharacter && isLengthValid;
    }

    private boolean notContains(String password, String reqChars) {
        return password.chars().noneMatch(c -> reqChars.indexOf(c) != -1);
    }

    private void addConstraintViolation(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }
}
