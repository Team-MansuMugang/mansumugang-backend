package org.mansumugang.mansumugang_service.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum InternalErrorType {
    ImageSaveError("이미지 저장에 실패하였습니다."),
    ImageDeleteError("이미지 삭제에 실패하였습니다."),

    RecordSaveError("녹음파일 저장에 실패하였습니다."),
    RecordDeleteError("녹음파일 삭제에 실패하였습니다."),
    RecordMetaDataError("녹음파일의 메타데이터를 읽을 수 없습니다."),
    NoSuchMedicineIntakeDayError("존재하지 않는 약 복용 요일입니다."),
    EMPTY_FILE_EXCEPTION("파일이 비어있습니다."),
    NO_FILENAME_ERROR("파일이름이 존재하지 않습니다"),
    NO_FILE_EXTENSION("파일의 확장자가 존재하지 않습니다."),
    INVALID_FILE_EXTENSION("유효하지 않은 파일 확장자 입니다."),
    S3_PUT_OBJECT_ERROR("S3에 파일 업로드를 실패하였습니다"),
    S3_DELETE_OBJECT_ERROR("S3 파일 삭제를 실패하였습니다.");

    private final String message;
}
