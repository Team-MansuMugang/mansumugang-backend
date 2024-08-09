package org.mansumugang.mansumugang_service.dto.record;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class RecordDelete {

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Dto{
        private String deletedFileName;

        public static Dto fromEntity(String fileName){
            return Dto.builder()
                    .deletedFileName(fileName)
                    .build();
        }

    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Response{
        private String message;
        private String deletedFileName;

        public static Response createNewResponse(Dto dto) {
            return builder()
                    .message("음성 녹음 파일 삭제가 완료되었습니다.")
                    .deletedFileName(dto.getDeletedFileName())
                    .build();
        }
    }
}
