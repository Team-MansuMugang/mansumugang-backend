package org.mansumugang.mansumugang_service.dto.record;

import lombok.*;
import org.mansumugang.mansumugang_service.domain.record.Record;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class RecordInquiry {
    @Getter
    @AllArgsConstructor
    @Builder
    public static class RecordElement {
        private String name;
        private String profileImageName;
        private Long recordId;
        private String recordFileName;
        private Long recordDuration;
        private String recordContent;
        private LocalDateTime uploadedTime;

        public static RecordElement fromEntity(Record record) {
            return RecordElement.builder()
                    .name(record.getPatient().getName())
                    .profileImageName(record.getPatient().getProfileImageName() != null ? record.getPatient().getProfileImageName() : null)
                    .recordId(record.getId())
                    .recordFileName(record.getFilename())
                    .recordDuration(record.getDuration())
                    .recordContent(record.getContent())
                    .uploadedTime(record.getCreatedAt())
                    .build();

        }

    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Dto {
        private String imageApiUrl;
        private String audioApiUrlPrefix;
        private List<RecordElement> records;

        public static Dto fromEntity(List<Record> foundAllRecords, String audioApiUrlPrefix, String imageApiUrl) {
            return Dto.builder()
                    .imageApiUrl(imageApiUrl)
                    .audioApiUrlPrefix(audioApiUrlPrefix)
                    .records(foundAllRecords.stream()
                            .map(record -> RecordInquiry.RecordElement.fromEntity(record))
                            .collect(Collectors.toList()))
                    .build();
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    public static class Response {
        private String imageApiUrl;
        private String audioApiUrlPrefix;
        private List<RecordElement> records;

        public static Response createNewResponse(Dto dto) {
            return Response.builder()
                    .imageApiUrl(dto.imageApiUrl)
                    .audioApiUrlPrefix(dto.audioApiUrlPrefix)
                    .records(dto.getRecords())
                    .build();
        }
    }
}
