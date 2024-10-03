package org.mansumugang.mansumugang_service.service.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mansumugang.mansumugang_service.constant.ErrorType;
import org.mansumugang.mansumugang_service.exception.CustomErrorException;
import org.mansumugang.mansumugang_service.provider.JwtTokenProvider;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogoutService {
    private final RedisTemplate<String, String> redisTemplate;
    private final JwtTokenProvider jwtTokenProvider;

    public void logout(String refreshToken){

        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();

        String resolvedRefreshToken = jwtTokenProvider.resolveToken(refreshToken);

        if (resolvedRefreshToken == null){
            throw new CustomErrorException(ErrorType.NotValidRefreshTokenError);
        }

        String savedAccessToken = valueOperations.get(resolvedRefreshToken);

        if (savedAccessToken == null){
            throw new CustomErrorException(ErrorType.NotValidRefreshTokenError);
        }

        valueOperations.getAndDelete(resolvedRefreshToken);
    }

}
