package org.mansumugang.mansumugang_service.dto.record;

import lombok.*;
import org.mansumugang.mansumugang_service.domain.record.Record;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class RecordInquiry {
    @Getter
    @AllArgsConstructor
    @Builder
    public static class RecordElement {
        private String name; // 환자 이름
        private Long recordId;
        private String recordFileName;
        private Long recordDuration;
        private LocalDateTime uploadedTime;
        private String audioApiUrlPrefix;

        public static RecordElement fromEntity(Record record, String audioApiUrlPrefix) {
            return RecordElement.builder()
                    .name(record.getPatient().getName())
                    .recordId(record.getId())
                    .recordFileName(record.getFilename())
                    .recordDuration(record.getDuration())
                    .uploadedTime(record.getCreatedAt())
                    .audioApiUrlPrefix(audioApiUrlPrefix)
                    .build();

        }

    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Dto {
        private List<RecordElement> records;

        public static Dto fromEntity(List<Record> foundAllRecords, String audioApiUrlPrefix) {
            return Dto.builder()
                    .records(foundAllRecords.stream()
                            .map(record -> RecordInquiry.RecordElement.fromEntity(record, audioApiUrlPrefix))
                            .collect(Collectors.toList()))
                    .build();
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    public static class Response {
        private List<RecordElement> records;

        public static Response createNewResponse(Dto dto) {
            return Response.builder()
                    .records(dto.getRecords())
                    .build();
        }
    }
}
