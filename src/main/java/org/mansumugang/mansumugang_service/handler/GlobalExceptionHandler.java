package org.mansumugang.mansumugang_service.handler;


import org.mansumugang.mansumugang_service.exception.CustomErrorException;
import org.mansumugang.mansumugang_service.exception.ErrorResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(CustomErrorException.class)
    public ResponseEntity<ErrorResponseDto> handleCustomErrorException(CustomErrorException ex) {
        ErrorResponseDto errorResponseDto = ErrorResponseDto.fromException(ex);
        return new ResponseEntity<>(errorResponseDto, ex.getErrorType().getHttpStatus());
    }
}
