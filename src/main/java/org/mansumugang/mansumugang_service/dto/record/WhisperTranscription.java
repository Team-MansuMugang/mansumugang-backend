package org.mansumugang.mansumugang_service.dto.record;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

public class WhisperTranscription implements Serializable {

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Request implements Serializable {

        private String model;
        private MultipartFile file;
    }

    @Data
    public static class Response implements Serializable {
        private String text;
    }
}
