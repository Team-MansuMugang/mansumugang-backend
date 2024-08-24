package org.mansumugang.mansumugang_service.service;


import lombok.RequiredArgsConstructor;
import org.mansumugang.mansumugang_service.model.request.*;
import org.mansumugang.mansumugang_service.model.response.ChatGPTResponse;
import org.mansumugang.mansumugang_service.model.response.WhisperTranscriptionResponse;
import org.mansumugang.mansumugang_service.openaiclient.OpenAIClient;
import org.mansumugang.mansumugang_service.openaiclient.OpenAIClientConfig;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class OpenAIClientService {

    private final OpenAIClient openAIClient;
    private final OpenAIClientConfig openAIClientConfig;

    private final static String ROLE_USER = "user";

    public ChatGPTResponse chat(ChatRequest chatRequest){
        Message message = Message.builder()
                .role(ROLE_USER)
                .content(chatRequest.getQuestion())
                .build();
        ChatGPTRequest chatGPTRequest = ChatGPTRequest.builder()
                .model(openAIClientConfig.getModel())
                .messages(Collections.singletonList(message))
                .build();
        return openAIClient.chat(chatGPTRequest);
    }

    public WhisperTranscriptionResponse createTranscription(TranscriptionRequest transcriptionRequest){
        WhisperTranscriptionRequest whisperTranscriptionRequest = WhisperTranscriptionRequest.builder()
                .model(openAIClientConfig.getAudioModel())
                .file(transcriptionRequest.getFile())
                .build();
        return openAIClient.createTranscription(whisperTranscriptionRequest);
    }
}
