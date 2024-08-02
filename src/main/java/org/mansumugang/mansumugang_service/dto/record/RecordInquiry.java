package org.mansumugang.mansumugang_service.dto.record;

import lombok.*;
import org.mansumugang.mansumugang_service.domain.record.Record;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class RecordInquiry {

    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    public static class Dto{

        private String name; // 환자 이름
        private Long recordId;
        private String recordFileName;
        private Long recordDuration;
        private LocalDateTime uploadedTime;
        private String savedPath;

        public static Dto fromEntity(Record record){

            return Dto.builder()
                    .name(record.getPatient().getName())
                    .recordId(record.getId())
                    .recordFileName(record.getFilename())
                    .recordDuration(record.getDuration())
                    .uploadedTime(record.getCreatedAt())
                    .savedPath(record.getSavedPath())
                    .build();
        }

    }

    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    public static class Response{
        private String name; // 환자 이름
        private Long recordId;
        private String recordFileName;
        private Long recordDuration;
        private LocalDateTime uploadedTime;
        private String savedPath;

        public static List<Response> createNewResponse(List<Dto> recordInquiryDtos){
            return recordInquiryDtos.stream()
                    .map(dto -> new Response(
                            dto.getName(),
                            dto.getRecordId(),
                            dto.getRecordFileName(),
                            dto.getRecordDuration(),
                            dto.getUploadedTime(),
                            dto.getSavedPath()))
                    .collect(Collectors.toList());
        }
    }


}
