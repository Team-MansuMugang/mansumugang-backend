package org.mansumugang.mansumugang_service.exception;


import lombok.extern.slf4j.Slf4j;
import org.mansumugang.mansumugang_service.constant.ErrorType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.List;

@Slf4j
@RestControllerAdvice(annotations = {RestController.class})
public class GlobalExceptionHandler {

    // 1. CustomErrorException 을 처리하는 메서드
    @ExceptionHandler(CustomErrorException.class)
    protected ResponseEntity<ErrorResponseDto> handleCustomErrorException(CustomErrorException e) {
        log.error(e.getMessage());
        ErrorResponseDto errorResponseDto = ErrorResponseDto.fromException(e);
        return new ResponseEntity<>(errorResponseDto, e.getErrorType().getHttpStatus());
    }

    // 2. MethodArgumentTypeMismatchException 을 처리하는 메서드
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<NotValidRequestErrorResponseDto> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        NotValidRequestErrorResponseDto.ErrorDescription errorDescription
                = NotValidRequestErrorResponseDto.ErrorDescription.of(ErrorType.QueryParamTypeMismatchError.getMessage());
        List<NotValidRequestErrorResponseDto.ErrorDescription> ErrorDescriptions
                = List.of(errorDescription);

        NotValidRequestErrorResponseDto notValidRequestErrorResponseDto
                = NotValidRequestErrorResponseDto.of(ErrorDescriptions);

        return new ResponseEntity<>(notValidRequestErrorResponseDto, HttpStatus.BAD_REQUEST);
    }

    // 3. MissingServletRequestParameterException 을 처리하는 메서드
    @ExceptionHandler(MissingServletRequestParameterException.class)
    protected ResponseEntity<NotValidRequestErrorResponseDto> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        NotValidRequestErrorResponseDto.ErrorDescription errorDescription
                = NotValidRequestErrorResponseDto.ErrorDescription.of(ErrorType.MissingQueryParamError.getMessage());
        List<NotValidRequestErrorResponseDto.ErrorDescription> ErrorDescriptions
                = List.of(errorDescription);

        NotValidRequestErrorResponseDto notValidRequestErrorResponseDto
                = NotValidRequestErrorResponseDto.of(ErrorDescriptions);

        return new ResponseEntity<>(notValidRequestErrorResponseDto, HttpStatus.BAD_REQUEST);
    }

    // 4. AccessDeniedException 을 처리하는 메서드
    @ExceptionHandler(AccessDeniedException.class)
    protected ResponseEntity<ErrorResponseDto> handleAccessDeniedException(AccessDeniedException e) {
        ErrorResponseDto errorResponseDto =
                ErrorResponseDto.of(
                        ErrorType.AccessDeniedError.name(),
                        ErrorType.AccessDeniedError.getMessage()
                );

        return new ResponseEntity<>(errorResponseDto, ErrorType.AccessDeniedError.getHttpStatus());
    }

    // 5. BindException 을 처리하는 메서드
    @ExceptionHandler(BindException.class)
    protected ResponseEntity<NotValidRequestErrorResponseDto> handleBindException(BindException e) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();

        List<NotValidRequestErrorResponseDto.ErrorDescription> errorDescriptions =
                fieldErrors.stream().map(NotValidRequestErrorResponseDto.ErrorDescription::of).toList();

        NotValidRequestErrorResponseDto notValidRequestErrorResponseDto =
                NotValidRequestErrorResponseDto.of(e, errorDescriptions);

        return new ResponseEntity<>(notValidRequestErrorResponseDto, HttpStatus.BAD_REQUEST);
    }

    // 6. InternalAuthenticationServiceException 을 처리하는 메서드
    @ExceptionHandler(InternalAuthenticationServiceException.class)
    protected ResponseEntity<ErrorResponseDto> HandleInternalAuthenticationServiceException(Exception e) {
        ErrorResponseDto errorResponseDto =
                ErrorResponseDto.of(
                        ErrorType.InternalServerError.name(),
                        ErrorType.InternalServerError.getMessage()
                );

        return new ResponseEntity<>(errorResponseDto, ErrorType.InternalServerError.getHttpStatus());
    }


    // 7. 그 외 일반적인 예외를 처리하는 메서드
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponseDto> HandleGeneralException(Exception e) {
        log.error(e.getMessage());
        ErrorResponseDto errorResponseDto =
                ErrorResponseDto.of(
                        ErrorType.InternalServerError.name(),
                        ErrorType.InternalServerError.getMessage()
                );

        return new ResponseEntity<>(errorResponseDto, ErrorType.InternalServerError.getHttpStatus());
    }

}
