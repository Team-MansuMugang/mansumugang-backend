package org.mansumugang.mansumugang_service.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
public class CustomUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {


    private static final String CONTENT_TYPE = "application/json";
    private static final String USERNAME_KEY = "username";
    private static final String PASSWORD_KEY = "password";

    private final ObjectMapper objectMapper = new ObjectMapper();


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        if (request.getContentType() == null || !request.getContentType().equals(CONTENT_TYPE)){
            throw new AuthenticationServiceException("해당 요청타입은 인증에 지원되지않습니다. 요청 타입 : " + request.getContentType());
        }

        try{
            String messageBody = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);

            Map<String, String> usernamePasswordMap = objectMapper.readValue(messageBody, Map.class);

            String username = usernamePasswordMap.get(USERNAME_KEY);
            String password = usernamePasswordMap.get(PASSWORD_KEY);

            UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);

            return this.getAuthenticationManager().authenticate(authRequest);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
