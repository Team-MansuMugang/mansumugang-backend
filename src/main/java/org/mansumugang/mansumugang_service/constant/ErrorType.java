package org.mansumugang.mansumugang_service.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorType {

    // ----- Common ------
    NotValidRequestError(
            HttpStatus.BAD_REQUEST, "유효하지 않은 요청입니다."
    ),
    QueryParamTypeMismatchError(
            HttpStatus.BAD_REQUEST, "해당 쿼리 파라미터의 타입이 올바르지 않습니다."
    ),
    MissingQueryParamError(
            HttpStatus.BAD_REQUEST, "해당 파라미터의 값이 존재하지 않습니다.."
    ),
    AccessDeniedError(
            HttpStatus.FORBIDDEN, "접근할 수 없는 권한을 가진 사용자입니다."
    ),
    InternalServerError(
            HttpStatus.INTERNAL_SERVER_ERROR, "알 수 없는 오류가 발생하였습니다. 문제가 지속되면 관리자에게 문의하세요."
    ),


    // ---- User ----
    DuplicatedUsernameError(
            HttpStatus.CONFLICT, "중복된 아이디입니다."
    ),
    DuplicatedNicknameError(
            HttpStatus.CONFLICT, "중복된 닉네임입니다"
    ),
    NotEqualPasswordAndPasswordCheck(
            HttpStatus.BAD_REQUEST, "패스워드와 패스워드 재입력이 일치하지 않습니다."
    ),
    UserNotFoundError(
            HttpStatus.NOT_FOUND, "유저 정보를 찾을 수 없습니다."
    ),

    // ----- Location ------
    OutOfBoundaryError(
            HttpStatus.BAD_REQUEST,"경위도가 범위(대한민국 내)를 벗어났습니다."
    ),

    UserLocationInfoNotFoundError(
            HttpStatus.NOT_FOUND, "유저는 존재하지만, 유저의 위치 정보가 존재하지 않습니다."
    ),

    // ----- Token ------
    NotValidAccessTokenError(
            HttpStatus.UNAUTHORIZED, "유효하지 않은 AccessToken입니다."
    ),
    NotExpiredAccessTokenError(
            HttpStatus.UNAUTHORIZED, "만료되지 않은 AccessToken입니다."
    ),
    ExpiredAccessTokenError(
            HttpStatus.UNAUTHORIZED, "만료된 AccessToken입니다."
    ),
    NoSuchAccessTokenError(
            HttpStatus.UNAUTHORIZED, "존재하지 않은 AccessToken입니다."
    ),
    NotValidRefreshTokenError(
            HttpStatus.UNAUTHORIZED, "유효하지 않은 RefreshToken입니다."
    ),
    NotExpiredRefreshTokenError(
            HttpStatus.UNAUTHORIZED, "만료되지 않은 RefreshToken입니다."
    ),
    ExpiredRefreshTokenError(
            HttpStatus.UNAUTHORIZED, "만료된 RefreshToken입니다."
    ),
    NoSuchRefreshTokenError(
            HttpStatus.UNAUTHORIZED, "존재하지 않은 RefreshToken입니다."
    ),;


    private final HttpStatus httpStatus;
    private final String message;

}
