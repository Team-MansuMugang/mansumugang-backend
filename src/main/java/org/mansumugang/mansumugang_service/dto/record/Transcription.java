package org.mansumugang.mansumugang_service.dto.record;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

public class Transcription implements Serializable {

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Request implements Serializable {

        private MultipartFile file;
    }
}
