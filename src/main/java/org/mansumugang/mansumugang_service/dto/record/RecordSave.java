package org.mansumugang.mansumugang_service.dto.record;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.mansumugang.mansumugang_service.domain.record.Record;

public class RecordSave {

    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    public static class Dto{
        private String recordFileName;
        private Long recordDuration;
        private String recordContent; // 음성파일 내용 텍스트 변환 결과 추가
        private String name;

        public static Dto getInfo(Record newRecord){
            return Dto.builder()
                    .name(newRecord.getPatient().getName())
                    .recordFileName(newRecord.getFilename())
                    .recordDuration(newRecord.getDuration())
                    .recordContent(newRecord.getContent())
                    .build();
        }
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    public static class Response{
        private String message;
        private String recordFileName;
        private Long recordDuration;
        private String recordContent;
        private String name;

        public static Response createNewResponse(Dto savedInfo) {
            return Response.builder()
                    .message("녹음파일을 성공적으로 저장하였습니다.")
                    .name(savedInfo.getName())
                    .recordFileName(savedInfo.getRecordFileName())
                    .recordDuration(savedInfo.getRecordDuration())
                    .recordContent(savedInfo.getRecordContent())
                    .build();
        }
    }
}
