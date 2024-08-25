package org.mansumugang.mansumugang_service.service.user;


import lombok.RequiredArgsConstructor;
import org.mansumugang.mansumugang_service.config.OpenAIClient;
import org.mansumugang.mansumugang_service.config.OpenAIClientConfig;
import org.mansumugang.mansumugang_service.dto.record.Transcription;
import org.mansumugang.mansumugang_service.dto.record.WhisperTranscription;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OpenAIClientService {

    private final OpenAIClient openAIClient;
    private final OpenAIClientConfig openAIClientConfig;


    public WhisperTranscription.Response createTranscription(Transcription.Request transcriptionRequest){
        WhisperTranscription.Request whisperTranscriptionRequest = WhisperTranscription.Request.builder()
                .model(openAIClientConfig.getAudioModel())
                .file(transcriptionRequest.getFile())
                .build();
        return openAIClient.createTranscription(whisperTranscriptionRequest);
    }
}
