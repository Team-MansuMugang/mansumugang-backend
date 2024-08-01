package org.mansumugang.mansumugang_service.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum InternalErrorType {
    ImageSaveError("이미지 저장에 실패하였습니다."),
    ImageDeleteError("이미지 삭제에 실패하였습니다.");

    private final String message;
}
