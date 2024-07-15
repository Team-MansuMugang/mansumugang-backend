package org.mansumugang.mansumugang_service.filter;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mansumugang.mansumugang_service.constant.ErrorType;
import org.mansumugang.mansumugang_service.constant.TokenType;
import org.mansumugang.mansumugang_service.exception.CustomErrorException;
import org.mansumugang.mansumugang_service.exception.ErrorResponseDto;
import org.mansumugang.mansumugang_service.provider.JwtTokenProvider;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain
    ) throws ServletException, IOException {
        log.info("JwtAuthenticationFilter 호출");
        // 1. request header 에서 jwt 토큰 추츨
        String accessToken = jwtTokenProvider.resolveToken(request.getHeader("Authorization"));

        if(accessToken == null){
            chain.doFilter(request, response);
            return;
        }

        // 2. validateToken 으로 유효성 검사
        try{
            jwtTokenProvider.validateToken(TokenType.ACCESS_TOKEN, accessToken);
            Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            chain.doFilter(request, response);
        }catch (Exception e){

            if (request.getRequestURI().equals("/api/auth/refreshToken") && request.getMethod().equals(HttpMethod.POST.name())) {
                chain.doFilter(request, response);
            }
            if (request.getRequestURI().equals("/api/auth/logout") && request.getMethod().equals(HttpMethod.POST.name())) {
                chain.doFilter(request, response);
            }
            // user-location 추가
            if (request.getRequestURI().equals("/api/location/user/\\d+") && request.getMethod().equals(HttpMethod.POST.name())) {
                chain.doFilter(request, response);
            }

            jwtExceptionHandler(response, e);
        }

    }

    public void jwtExceptionHandler(HttpServletResponse response, Exception e) {
        log.info("Exception Info:" + e.getMessage());
        ErrorType errorType = ErrorType.InternalServerError;
        if (e instanceof CustomErrorException) {
            errorType = ((CustomErrorException) e).getErrorType();
        }

        response.setStatus(errorType.getHttpStatus().value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        try {
            String json = new ObjectMapper().writeValueAsString(ErrorResponseDto.of(errorType.name(), errorType.getMessage()));
            response.getWriter().write(json);
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
    }

}
