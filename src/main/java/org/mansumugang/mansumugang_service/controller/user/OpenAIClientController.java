package org.mansumugang.mansumugang_service.controller.user;

import lombok.RequiredArgsConstructor;
import org.mansumugang.mansumugang_service.dto.record.Transcription;
import org.mansumugang.mansumugang_service.dto.record.WhisperTranscription;
import org.mansumugang.mansumugang_service.service.record.OpenAIClientService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1")
public class OpenAIClientController {

    private final OpenAIClientService openAIClientService;


    @PostMapping(value = "/transcription", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public WhisperTranscription.Response createTranscription(@ModelAttribute Transcription.Request transcriptionRequest){
        return openAIClientService.createTranscription(transcriptionRequest);
    }
}
