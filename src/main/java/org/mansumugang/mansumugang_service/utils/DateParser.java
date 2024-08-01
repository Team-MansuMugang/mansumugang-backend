package org.mansumugang.mansumugang_service.utils;

import org.mansumugang.mansumugang_service.exception.CustomNotValidErrorException;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Component
public class DateParser {
    public LocalDate parseDate(String dateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try {
            return LocalDate.parse(dateStr, formatter);
        } catch (DateTimeParseException e) {
            throw new CustomNotValidErrorException("medicineIntakeStopDay", "유효하지 않은 날짜 형식입니다.");
        }
    }

    public LocalDateTime parseDateTime(String dateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd:hh:mm:ss");
        try {
            return LocalDateTime.parse(dateStr, formatter);
        } catch (DateTimeParseException e) {
            throw new CustomNotValidErrorException("medicineIntakeStopDay", "유효하지 않은 날짜 형식입니다.");
        }
    }
}
