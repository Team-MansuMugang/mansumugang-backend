package org.mansumugang.mansumugang_service.exception;

import lombok.Getter;

@Getter
public class CustomNotValidErrorException extends RuntimeException {
    private final String field;
    public CustomNotValidErrorException(String field, String message) {
        super(message);
        this.field = field;
    }
}
