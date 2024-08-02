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
    public static class SavedInfo{
        private String recordFileName;
        private Long recordDuration;
        private String name;

        public static SavedInfo getInfo(Record newRecord){
            return SavedInfo.builder()
                    .name(newRecord.getPatient().getName())
                    .recordFileName(newRecord.getFilename())
                    .recordDuration(newRecord.getDuration())
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
        private String name;

        public static Response createNewResponse(SavedInfo savedInfo) {
            return Response.builder()
                    .message("녹음파일을 성공적으로 저장하였습니다.")
                    .name(savedInfo.getName())
                    .recordFileName(savedInfo.getRecordFileName())
                    .recordDuration(savedInfo.getRecordDuration())
                    .build();
        }
    }
}
