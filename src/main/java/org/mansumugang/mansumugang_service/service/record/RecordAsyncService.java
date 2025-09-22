package org.mansumugang.mansumugang_service.service.record;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mansumugang.mansumugang_service.domain.record.Record;
import org.mansumugang.mansumugang_service.dto.record.Transcription;
import org.mansumugang.mansumugang_service.dto.record.WhisperTranscription;
import org.mansumugang.mansumugang_service.repository.RecordRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class RecordAsyncService {
    private final OpenAIClientService openAIClientService;
    private final RecordRepository recordRepository;

    @Async
    public void updateTranscriptionAsync(Record record, Transcription.Request request) {
        try {
            WhisperTranscription.Response transcription = openAIClientService.createTranscription(request);
            String transcriptionText = transcription.getText();

            record.setContent(transcriptionText);
            recordRepository.save(record);
        } catch (Exception e) {
            log.error("Failed to transcribe audio for record {}", e.getMessage());
            record.setContent("음성변환에 실패하였습니다.");
            recordRepository.save(record);
        }
    }
}
