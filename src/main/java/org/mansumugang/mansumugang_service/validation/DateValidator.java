package org.mansumugang.mansumugang_service.validation;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateValidator implements ConstraintValidator<ValidDate, String> {

    private String datePattern;

    @Override
    public void initialize(ValidDate constraintAnnotation) {
        this.datePattern = constraintAnnotation.pattern();
    }

    @Override
    public boolean isValid(String dateStr, ConstraintValidatorContext context) {
        if (dateStr == null || dateStr.isEmpty()) {
            return true; // null 또는 빈 문자열은 다른 어노테이션에서 검증
        }

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(datePattern);
        try {
            LocalDate.parse(dateStr, dateFormatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
