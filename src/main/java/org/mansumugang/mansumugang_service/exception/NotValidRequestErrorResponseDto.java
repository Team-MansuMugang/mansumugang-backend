package org.mansumugang.mansumugang_service.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.mansumugang.mansumugang_service.constant.ErrorType;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class NotValidRequestErrorResponseDto {
    private String errorType;
    private String message;
    private List<ErrorDescription> errorDescriptions;

    public static NotValidRequestErrorResponseDto of(List<ErrorDescription> errorDescription){
        return NotValidRequestErrorResponseDto.builder()
                .errorType(ErrorType.NotValidRequestError.name())
                .message(ErrorType.NotValidRequestError.getMessage())
                .errorDescriptions(errorDescription)
                .build();
    }

    public static NotValidRequestErrorResponseDto of(BindException e, List<ErrorDescription> errorDescription){
        return NotValidRequestErrorResponseDto.builder()
                .errorType(ErrorType.NotValidRequestError.name())
                .message(ErrorType.NotValidRequestError.getMessage())
                .errorDescriptions(errorDescription)
                .build();
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    public static class ErrorDescription{
        private String field;
        private String message;

        public static ErrorDescription of(String field, String message){
            return ErrorDescription.builder()
                    .field(field)
                    .message(message)
                    .build();
        }


        public static ErrorDescription of(FieldError error){
            return ErrorDescription.builder()
                    .field(error.getField())
                    .message(error.getDefaultMessage())
                    .build();
        }
    }


}