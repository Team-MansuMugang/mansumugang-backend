package org.mansumugang.mansumugang_service.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum InternalErrorType {
    FileSaveError("파일 저장에 실패하였습니다."),
    FileDeleteError("파일 삭제에 실패하였습니다."),
    RecordMetaDataError("녹음파일의 메타데이터를 읽을 수 없습니다."),
    NoSuchMedicineIntakeDayError("존재하지 않는 약 복용 요일입니다."),

    EmptyFileError("파일이 비어있습니다."),
    NoFilenameError("파일이름이 존재하지 않습니다"),

    NoFileExtension("파일의 확장자가 존재하지 않습니다."),
    InvalidFileExtension("유효하지 않은 파일 확장자 입니다.");

    private final String message;
}
