package org.mansumugang.mansumugang_service.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class ErrorResponseDto {

    private String errorType;
    private String message;

    public static ErrorResponseDto fromException(CustomErrorException e){
        return ErrorResponseDto.builder()
                .errorType(e.getErrorType().name())
                .message(e.getMessage())
                .build();
    }

    public static ErrorResponseDto of(String errorType, String message){
        return ErrorResponseDto.builder()
                .errorType(errorType)
                .message(message)
                .build();
    }
}
