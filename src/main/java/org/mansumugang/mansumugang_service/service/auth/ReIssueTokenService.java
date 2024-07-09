package org.mansumugang.mansumugang_service.service.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mansumugang.mansumugang_service.constant.ErrorType;
import org.mansumugang.mansumugang_service.constant.TokenType;
import org.mansumugang.mansumugang_service.dto.auth.token.ReissueTokenDto;
import org.mansumugang.mansumugang_service.exception.CustomErrorException;
import org.mansumugang.mansumugang_service.provider.JwtTokenProvider;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReIssueTokenService {
    private final RedisTemplate<String, String> redisTemplate;
    private final JwtTokenProvider jwtTokenProvider;

    public ReissueTokenDto reissueToken(String accessToken, String refreshToken){
        log.info("reissueToken 호출");

        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();

        // 1. 토큰 리졸브
        String resolvedAccessToken = jwtTokenProvider.resolveToken(accessToken);
        String resolvedRefreshToken = jwtTokenProvider.resolveToken(refreshToken);

        log.info("resolve 된 accessToken={}", resolvedAccessToken);
        log.info("resolve 된 refreshToken={}", resolvedRefreshToken);

        // resolve 된 access/refresh token 이 null 이라면 예외 발생
        if (resolvedAccessToken == null || resolvedRefreshToken == null){
            throw new CustomErrorException(ErrorType.NotValidRequestError);
        }

        // 2. redis 에 저장된 토큰 가져오기
        String savedAccessToken = valueOperations.get(resolvedRefreshToken);

        if(savedAccessToken == null){
            throw new CustomErrorException(ErrorType.NoSuchRefreshTokenError);
        }

        log.info("토큰 가져오기 성공, savedAccessToken={}", savedAccessToken);

        // 3. refreshToken 유효성 검사
        log.info("refreshToken 유효성 검사 시작");
        boolean isRefreshTokenExpired = jwtTokenProvider.isTokenExpired(TokenType.REFRESH_TOKEN, resolvedRefreshToken);

        if (isRefreshTokenExpired){
            valueOperations.getAndDelete(resolvedRefreshToken);
            throw new CustomErrorException(ErrorType.ExpiredRefreshTokenError);
        }

        if (!resolvedAccessToken.equals(savedAccessToken)){
            valueOperations.getAndDelete(resolvedRefreshToken);
            throw new CustomErrorException(ErrorType.NoSuchAccessTokenError);
        }
        log.info("refreshToken 유효성 검사 완료");

        // 4. accessToken 유효성 검사
        log.info("acessToken 유효성 검사 시작");
        boolean isAccessTokenExpired = jwtTokenProvider.isTokenExpired(TokenType.ACCESS_TOKEN, resolvedAccessToken);

        if (!isAccessTokenExpired){
            valueOperations.getAndDelete(resolvedRefreshToken);
            throw new CustomErrorException(ErrorType.NotExpiredAccessTokenError);
        }
        log.info("accessToken 유효성 검사 완료");

        // 5. 토큰 재발행
        String reissueToken = jwtTokenProvider.reIssueToken(resolvedAccessToken);
        valueOperations.getAndDelete(resolvedRefreshToken);
        valueOperations.set(resolvedRefreshToken, resolvedAccessToken);

        return ReissueTokenDto.of(reissueToken);
    }
}
