package org.mansumugang.mansumugang_service.exception;

import lombok.Getter;
import org.mansumugang.mansumugang_service.constant.InternalErrorType;

@Getter
public class InternalErrorException extends RuntimeException {
    private final InternalErrorType internalErrorType;

    public InternalErrorException(InternalErrorType internalError) {
        super(internalError.getMessage());
        this.internalErrorType = internalError;
    }
}
