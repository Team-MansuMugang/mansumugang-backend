package org.mansumugang.mansumugang_service.config;

import org.mansumugang.mansumugang_service.dto.record.WhisperTranscription;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(
        name = "openai-service",
        url = "${openai-service.urls.base-url}",
        configuration = OpenAIClientConfig.class
)
public interface OpenAIClient {


    @PostMapping(value = "${openai-service.urls.create-transcription-url}", headers = {"Content-Type=multipart/form-data"})
    WhisperTranscription.Response createTranscription(@ModelAttribute WhisperTranscription.Request whisperTranscriptionRequest);
}
