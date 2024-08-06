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
    NoSuchMedicineIntakeDayError("존재하지 않는 약 복용 요일입니다.");

    private final String message;
}
