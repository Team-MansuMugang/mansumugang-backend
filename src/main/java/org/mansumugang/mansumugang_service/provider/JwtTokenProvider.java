package org.mansumugang.mansumugang_service.provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.mansumugang.mansumugang_service.constant.ErrorType;
import org.mansumugang.mansumugang_service.constant.TokenType;
import org.mansumugang.mansumugang_service.domain.user.User;
import org.mansumugang.mansumugang_service.dto.auth.login.LoginResponseDto;
import org.mansumugang.mansumugang_service.exception.CustomErrorException;
import org.mansumugang.mansumugang_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {

    private final Key key; // 서명에 필요한 키
    private final UserRepository userRepository;

    @Value("${jwt.access.header}")
    private String accessHeader; // AccessToken 을 전달하는 헤더의 이름

    @Value("${jwt.refresh.header}")
    private String refreshHeader; // RefreshToken 을 전달하는 헤더의 이름

    @Value("${jwt.access.expiration}")
    private Long accessTokenExpirationPeriod; // AccessToken 유효기간

    @Value("${jwt.refresh.expiration}")
    private Long refreshTokenExpirationPeriod; // RefreshToken 유효기간


    // 생성자로 key, userRepository 초기화
    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey, UserRepository userRepository) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.userRepository = userRepository;
    }

    // 유저 정보 -> AccessToken, RefreshToken 생성 메서드
    // AccessToken 생성
    public String generateAccessToken(Authentication authentication){
        Claims claims = Jwts.claims().setSubject(authentication.getName());
        Date now = new Date();

        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + accessTokenExpirationPeriod))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // RefreshToken 생성
    public String generateRefreshToken(){
        Date now = new Date();

        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setExpiration(new Date(now.getTime() + refreshTokenExpirationPeriod))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }


    // JwtToken 복호화 -> 토큰에 있는 정보를 꺼내는 메서드
    public Authentication getAuthentication(String accessToken){
        // 토큰 복호화
        Claims claims = parseClaims(accessToken);

        // 복호화한 클레임에 사용자 id 의 값이 null 이라면 에러발생
        if(claims.get("sub")==null){
            throw new CustomErrorException(ErrorType.NotValidAccessTokenError);
        }

        // AccessToken 의 sub 존재 O
        String username = claims.get("sub").toString(); // 복호화된 accessToken 에서 사용자 id 추출
        User user = userRepository.findByUsername(username).orElseThrow(()->new CustomErrorException(ErrorType.UserNotFoundError));

        return new UsernamePasswordAuthenticationToken(user, "", user.getAuthorities());
    }

    // 토큰을 검증하는 메서드 : access / refresh
    public void validateToken(TokenType tokenType, String token){
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);

            // 잘못된 토큰 형식이나 지원되지 않는 토큰 형식, 또는 기타 보안 예외가 발생한 경우.
        } catch (SecurityException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            if (tokenType == TokenType.ACCESS_TOKEN) throw new CustomErrorException(ErrorType.NotValidAccessTokenError);
            if (tokenType == TokenType.REFRESH_TOKEN) throw new CustomErrorException(ErrorType.NotValidRefreshTokenError);

            // 토큰이 만료된 경우
        } catch (ExpiredJwtException e) {
            if (tokenType == TokenType.ACCESS_TOKEN) throw new CustomErrorException(ErrorType.ExpiredAccessTokenError);
            if (tokenType == TokenType.REFRESH_TOKEN) throw new CustomErrorException(ErrorType.ExpiredRefreshTokenError);
        }
    }

    // 토큰이 만료되었는지 확인하는 메서드
    public boolean isTokenExpired(TokenType tokenType, String token){
        try{
            validateToken(tokenType, token);

        }catch (CustomErrorException e){
            if(e.getErrorType() == ErrorType.ExpiredAccessTokenError || e.getErrorType() == ErrorType.ExpiredRefreshTokenError){
                return true;
            }
            throw e;
        }
        return false;
    }

    // 토큰을 문자열에서 추출
    public String resolveToken(String value){

        // 문자열이 비어있지 않고, Bearer 로 시작한다면 Bearer 을 제외한 나머지 문자열 반환
        if(StringUtils.hasText(value) && value.startsWith("Bearer")){
            return value.substring(7);
        }

        return null;
    }

    // Access/Refresh 토큰을 응답으로 반환하는 역할
    public void responseAccessAndRefreshToken(HttpServletResponse response, String accessToken, String refreshToken, String userType){

        // 응답 속성 설정
        response.setStatus(HttpStatus.OK.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try{
            String json = new ObjectMapper().writeValueAsString(LoginResponseDto.of(accessToken, refreshToken, userType));
            response.getWriter().write(json);
        }catch (Exception e){
            log.error(e.getMessage());
        }

    }

    // 토큰 재발행 메서드
    public String reIssueToken(String accessToken){
        Authentication authentication = getAuthentication(accessToken);
        Claims claims = Jwts.claims().setSubject(authentication.getName());
        Date now = new Date();

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + accessTokenExpirationPeriod))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }


    private Claims parseClaims(String accessToken){
        try{
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e){
            return e.getClaims();
        }
    }
}
