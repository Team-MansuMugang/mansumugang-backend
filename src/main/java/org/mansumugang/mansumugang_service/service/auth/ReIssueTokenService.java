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

        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();

        String resolvedAccessToken = jwtTokenProvider.resolveToken(accessToken);
        String resolvedRefreshToken = jwtTokenProvider.resolveToken(refreshToken);

        if (resolvedAccessToken == null){
            throw new CustomErrorException(ErrorType.NotValidAccessTokenError);
        }

        if(resolvedRefreshToken == null) {
            throw new CustomErrorException(ErrorType.NotValidRefreshTokenError);
        }

        String savedAccessToken = valueOperations.get(resolvedRefreshToken);

        if(savedAccessToken == null){
            throw new CustomErrorException(ErrorType.NoSuchRefreshTokenError);
        }

        boolean isRefreshTokenExpired = jwtTokenProvider.isTokenExpired(TokenType.REFRESH_TOKEN, resolvedRefreshToken);

        if (isRefreshTokenExpired){
            valueOperations.getAndDelete(resolvedRefreshToken);
            throw new CustomErrorException(ErrorType.ExpiredRefreshTokenError);
        }

        if (!resolvedAccessToken.equals(savedAccessToken)){
            valueOperations.getAndDelete(resolvedRefreshToken);
            throw new CustomErrorException(ErrorType.NoSuchAccessTokenError);
        }

        boolean isAccessTokenExpired = jwtTokenProvider.isTokenExpired(TokenType.ACCESS_TOKEN, resolvedAccessToken);

        if (!isAccessTokenExpired){
            valueOperations.getAndDelete(resolvedRefreshToken);
            throw new CustomErrorException(ErrorType.NotExpiredAccessTokenError);
        }

        String reissueToken = jwtTokenProvider.reIssueToken(resolvedAccessToken);
        valueOperations.getAndDelete(resolvedRefreshToken);
        valueOperations.set(resolvedRefreshToken, reissueToken);

        return ReissueTokenDto.of(reissueToken);
    }
}
