package org.mansumugang.mansumugang_service.exception;

import lombok.Getter;

@Getter
public class CustomNotValidErrorException extends RuntimeException {
    private final String field;
    public CustomNotValidErrorException(String message, String field) {
        super(message);
        this.field = field;
    }
}
