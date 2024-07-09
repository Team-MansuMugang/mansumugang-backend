package org.mansumugang.mansumugang_service.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.mansumugang.mansumugang_service.constant.ErrorType;
import org.mansumugang.mansumugang_service.exception.ErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import java.io.IOException;

@Slf4j
public class LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {

        log.info("LoginFailureHandler 호출");

        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        ErrorResponseDto errorResponseDto = ErrorResponseDto.of(ErrorType.InternalServerError.name(), ErrorType.InternalServerError.getMessage());

        if (exception instanceof BadCredentialsException ||
                exception instanceof UsernameNotFoundException ||
                exception instanceof InternalAuthenticationServiceException) {
            httpStatus = HttpStatus.BAD_REQUEST;
            errorResponseDto = ErrorResponseDto.of(ErrorType.UserNotFoundError.name(),"아이디 혹은 비밀번호를 다시 한번 확인해 주세요");
        }

        String jsonResponse = objectMapper.writeValueAsString(errorResponseDto);

        response.setStatus(httpStatus.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(jsonResponse);
    }
}
