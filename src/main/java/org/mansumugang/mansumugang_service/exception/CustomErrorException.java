package org.mansumugang.mansumugang_service.exception;

import lombok.Getter;
import org.mansumugang.mansumugang_service.constant.ErrorType;

@Getter
public class CustomErrorException extends RuntimeException{
    private final ErrorType errorType;
    public CustomErrorException(ErrorType errorType) {
        super(errorType.getMessage());
        this.errorType = errorType;
    }
}
