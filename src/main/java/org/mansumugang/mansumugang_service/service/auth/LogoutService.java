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
        log.info("LogoutService 호출");

        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();

        // 정상로직 1.
        // 요청으로 받은 refreshToken 을 resolve
        String resolvedRefreshToken = jwtTokenProvider.resolveToken(refreshToken);

        // resolve 된 refreshToken 이 null 이라면 에러 발생
        if (resolvedRefreshToken == null){
            throw new CustomErrorException(ErrorType.NotValidRefreshTokenError);
        }

        // 정상로직 2.
        // redis 에 저장된 accessToken 추출(refreshToken 으로 찾는 것)
        String savedAccessToken = valueOperations.get(resolvedRefreshToken);

        // 요청으로 받은 refreshToken 과 그에 맞는 accessToken 을 찾지 못하면 에러 발생
        if (savedAccessToken == null){
            throw new CustomErrorException(ErrorType.NotValidRefreshTokenError);
        }

        // 정상로직 3.
        // savedAccessToken 이 존재한다면 redis 에서 refreshToken 삭제
        valueOperations.getAndDelete(resolvedRefreshToken);
    }

}
