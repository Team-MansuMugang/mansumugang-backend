package org.mansumugang.mansumugang_service.restcontroller;

import lombok.RequiredArgsConstructor;
import org.mansumugang.mansumugang_service.model.request.ChatRequest;
import org.mansumugang.mansumugang_service.model.request.TranscriptionRequest;
import org.mansumugang.mansumugang_service.model.response.ChatGPTResponse;
import org.mansumugang.mansumugang_service.model.response.WhisperTranscriptionResponse;
import org.mansumugang.mansumugang_service.service.OpenAIClientService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1")
public class OpenAIClientController {

    private final OpenAIClientService openAIClientService;

    @PostMapping(value = "/chat", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ChatGPTResponse chat(@RequestBody ChatRequest chatRequest){
        return openAIClientService.chat(chatRequest);
    }

    @PostMapping(value = "/transcription", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public WhisperTranscriptionResponse createTranscription(@ModelAttribute TranscriptionRequest transcriptionRequest){
        return openAIClientService.createTranscription(transcriptionRequest);
    }
}
