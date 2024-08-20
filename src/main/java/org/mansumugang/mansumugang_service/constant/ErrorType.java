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
    UserTypeDismatchError(
            HttpStatus.NOT_FOUND, "해당 유저는 보호자가 아닌 환자입니다."
    ),

    // ----- Location ------
    OutOfBoundaryError(
            HttpStatus.BAD_REQUEST, "경위도가 범위(대한민국 내)를 벗어났습니다."
    ),

    UserLocationInfoNotFoundError(
            HttpStatus.NOT_FOUND, "유저는 존재하지만, 유저의 위치 정보가 존재하지 않습니다."
    ),

    UserLocationInfoWithinRangeNotFoundError(
            HttpStatus.NOT_FOUND, "유저는 존재하지만, 조회하려는 시간 범위 내 위치 정보가 존재하지 않습니다."
    ),
    NeedLatitudeAndLongitudeError(
            HttpStatus.BAD_REQUEST, "위도와 경도 정보가 필요합니다."
    ),

    // ----- Record ------
    RecordFileNotFound(
            HttpStatus.NOT_FOUND, "저장할 음성녹음 파일을 찾을 수 없습니다.."
    ),

    RecordInfoNotFound(
            HttpStatus.NOT_FOUND, "해당 고유번호를 가진 음성녹음 파일이 존재하지 않습니다."
    ),

    UserRecordInfoNotFoundError(
            HttpStatus.NOT_FOUND, "유저는 존재하지만, 유저의 음성녹음 정보가 존재하지 않습니다."
    ),

    // ----- Post -----
    NoSuchPostError(
            HttpStatus.NOT_FOUND, "존재하지 않은 게시물입니다."
    ),
    NotTheAuthorOfThePost(
            HttpStatus.UNAUTHORIZED, "게시물의 작성자가 아닙니다."
    ),

    NoRequestBodyError(
            HttpStatus.BAD_REQUEST, "request body가 전달되지 않았습니다."
    ),


    // ----- Category ------
    NoSuchCategoryError(
            HttpStatus.BAD_REQUEST, "존재하지 않은 카테고리입니다."
    ),

    // ----- Image ------
    NoImageFileError(
            HttpStatus.BAD_REQUEST, "유효하지 않은 이미지 파일입니다."
    ),

    // ---- Comment ----
    NoSuchCommentError(
            HttpStatus.NOT_FOUND, "존재하지 않는 댓글입니다."
    ),
    NotTheAuthorOfTheComment(
            HttpStatus.UNAUTHORIZED, "댓글의 작성자가 아닙니다."
    ),
    DeletedCommentError(
            HttpStatus.NOT_FOUND, "삭제된 댓글입니다"
    ),

    // ---- Reply ----
    NoSuchReplyError(
            HttpStatus.NOT_FOUND, "존재하지 않는 답글입니다."
    ),
    NotTheAuthorOfTheReply(
            HttpStatus.UNAUTHORIZED, "답글의 작성자가 아닙니다."
    ),
    DeletedReplyError(
            HttpStatus.NOT_FOUND, "삭제된 답글입니다"
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
    ),

    // ----- Medicine ------
    NoSuchMedicineInTakeCategoryError(
            HttpStatus.NOT_FOUND, "존재하지 않는 약 섭취 카테고리입니다"
    ),
    NoSuchMedicineError(
            HttpStatus.NOT_FOUND, "존재하지 않는 약입니다"
    ),
    NotExistMedicineScheduleError(
            HttpStatus.NOT_FOUND, "약에 대한 복용 간격이 존재하지 않습니다."
    ),
    NoSuchMedicineIntakeDayError(
            HttpStatus.NOT_FOUND, "존재하지 않은 약 복용 요일입니다."
    ),
    NoSuchMedicineIntakeTimeError(
            HttpStatus.NOT_FOUND, "존재하지 않은 약 복용 시간입니다."
    ),
    NoSuchDayTypeError(
            HttpStatus.BAD_REQUEST, "유호하지 않은 요일입니다."
    ),
    AlreadyExistMedicineIntakeTimeError(
            HttpStatus.CONFLICT, "이미 존재하는 약 복용시간입니다."
    ),
    AlreadyExistMedicineIntakeDayError(
            HttpStatus.CONFLICT, "이미 존재하는 약 복용요일입니다."
    ),
    ConditionOfNotBeingAbleToToggleError(
            HttpStatus.BAD_REQUEST, "Toggle이 가능한 조건이 아닙니다."
    ),

    // ----- Hospital ------
    NoSuchHospitalError(
            HttpStatus.NOT_FOUND, "존재하지 않은 병원 아이디 입니다."
    ),
    DuplicatedHospitalVisitingTimeError(
            HttpStatus.CONFLICT, "병원 방문 시간은 중복될 수 없습니다."
    )
    ;

    private final HttpStatus httpStatus;
    private final String message;

}
