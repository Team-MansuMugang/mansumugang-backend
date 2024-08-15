package org.mansumugang.mansumugang_service.handler;


import org.mansumugang.mansumugang_service.constant.ErrorType;
import org.mansumugang.mansumugang_service.exception.NotValidRequestErrorResponseDto;
import org.mansumugang.mansumugang_service.exception.CustomErrorException;
import org.mansumugang.mansumugang_service.exception.CustomNotValidErrorException;
import org.mansumugang.mansumugang_service.exception.ErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(CustomErrorException.class)
    public ResponseEntity<ErrorResponseDto> handleCustomErrorException(CustomErrorException ex) {
        ErrorResponseDto errorResponseDto = ErrorResponseDto.fromException(ex);
        return new ResponseEntity<>(errorResponseDto, ex.getErrorType().getHttpStatus());
    }

    @ExceptionHandler(CustomNotValidErrorException.class)
    protected ResponseEntity<NotValidRequestErrorResponseDto> handleCustomNotValidErrorException(CustomNotValidErrorException e) {
        NotValidRequestErrorResponseDto notValidRequestErrorResponseDto =
                NotValidRequestErrorResponseDto.of(
                        List.of(NotValidRequestErrorResponseDto.ErrorDescription.of(e.getField(), e.getMessage()))
                );

        return new ResponseEntity<>(notValidRequestErrorResponseDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<NotValidRequestErrorResponseDto> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        NotValidRequestErrorResponseDto.ErrorDescription errorDescription
                = NotValidRequestErrorResponseDto.ErrorDescription.of( ErrorType.QueryParamTypeMismatchError.name(), ErrorType.QueryParamTypeMismatchError.getMessage());
        List<NotValidRequestErrorResponseDto.ErrorDescription> ErrorDescriptions
                = List.of(errorDescription);

        NotValidRequestErrorResponseDto notValidRequestErrorResponseDto
                = NotValidRequestErrorResponseDto.of(ErrorDescriptions);

        return new ResponseEntity<>(notValidRequestErrorResponseDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    protected ResponseEntity<NotValidRequestErrorResponseDto> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        NotValidRequestErrorResponseDto.ErrorDescription errorDescription
                = NotValidRequestErrorResponseDto.ErrorDescription.of(ErrorType.MissingQueryParamError.name(), ErrorType.MissingQueryParamError.getMessage());
        List<NotValidRequestErrorResponseDto.ErrorDescription> ErrorDescriptions
                = List.of(errorDescription);

        NotValidRequestErrorResponseDto notValidRequestErrorResponseDto
                = NotValidRequestErrorResponseDto.of(ErrorDescriptions);

        return new ResponseEntity<>(notValidRequestErrorResponseDto, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(AccessDeniedException.class)
    protected ResponseEntity<ErrorResponseDto> handleAccessDeniedException(AccessDeniedException e) {
        ErrorResponseDto errorResponseDto =
                ErrorResponseDto.of(
                        ErrorType.AccessDeniedError.name(),
                        ErrorType.AccessDeniedError.getMessage()
                );

        return new ResponseEntity<>(errorResponseDto, ErrorType.AccessDeniedError.getHttpStatus());
    }

    @ExceptionHandler(BindException.class)
    protected ResponseEntity<NotValidRequestErrorResponseDto> handleBindException(BindException e) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();

        List<NotValidRequestErrorResponseDto.ErrorDescription> errorDescriptions =
                fieldErrors.stream().map(NotValidRequestErrorResponseDto.ErrorDescription::of).toList();

        NotValidRequestErrorResponseDto notValidRequestErrorResponseDto =
                NotValidRequestErrorResponseDto.of(e, errorDescriptions);

        return new ResponseEntity<>(notValidRequestErrorResponseDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InternalAuthenticationServiceException.class)
    protected ResponseEntity<ErrorResponseDto> HandleInternalAuthenticationServiceException(Exception e) {
        ErrorResponseDto errorResponseDto =
                ErrorResponseDto.of(
                        ErrorType.InternalServerError.name(),
                        ErrorType.InternalServerError.getMessage()
                );

        return new ResponseEntity<>(errorResponseDto, ErrorType.InternalServerError.getHttpStatus());
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponseDto> HandleGeneralException(Exception e) {
        ErrorResponseDto errorResponseDto =
                ErrorResponseDto.of(
                        ErrorType.InternalServerError.name(),
                        ErrorType.InternalServerError.getMessage()
                );

        return new ResponseEntity<>(errorResponseDto, ErrorType.InternalServerError.getHttpStatus());
    }
}