package org.mansumugang.mansumugang_service.dto.record;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;


public class RecordSaveLimit {

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Dto{
        private int dailyRecordingLimit;
        private int remainingRecordingCount;

        public static Dto fromEntity(int dailyRecordingLimit, int remainingRecordingCount){

            return Dto.builder()
                    .dailyRecordingLimit(dailyRecordingLimit)
                    .remainingRecordingCount(remainingRecordingCount)
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Response{
        private int dailyRecordingLimit;
        private int remainingRecordingCount;

        public static Response createNewResponse(Dto dto){
            return Response.builder()
                    .dailyRecordingLimit(dto.dailyRecordingLimit)
                    .remainingRecordingCount(dto.remainingRecordingCount)
                    .build();
        }
    }

}
