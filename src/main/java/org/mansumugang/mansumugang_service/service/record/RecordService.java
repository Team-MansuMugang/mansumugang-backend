package org.mansumugang.mansumugang_service.service.record;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mansumugang.mansumugang_service.constant.ErrorType;
import org.mansumugang.mansumugang_service.constant.InternalErrorType;
import org.mansumugang.mansumugang_service.domain.record.Record;
import org.mansumugang.mansumugang_service.domain.user.Patient;
import org.mansumugang.mansumugang_service.domain.user.Protector;
import org.mansumugang.mansumugang_service.domain.user.User;
import org.mansumugang.mansumugang_service.dto.file.AudioFileSaveDto;
import org.mansumugang.mansumugang_service.dto.record.*;
import org.mansumugang.mansumugang_service.exception.CustomErrorException;
import org.mansumugang.mansumugang_service.exception.InternalErrorException;
import org.mansumugang.mansumugang_service.repository.PatientRepository;
import org.mansumugang.mansumugang_service.repository.RecordRepository;
import org.mansumugang.mansumugang_service.service.file.FileService;
import org.mansumugang.mansumugang_service.service.user.UserCommonService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class RecordService {

    private final RecordRepository recordRepository;
    private final PatientRepository patientRepository;

    private final FileService fileService;

    private final OpenAIClientService openAIClientService;
    private final UserCommonService userCommonService;

    @Value("${file.upload.audio.api}")
    private String audioApiUrlPrefix;

    @Value("${file.upload.image.api}")
    private String imageApiUrl;

    @Transactional
    public RecordSave.Dto saveRecord(User user, Transcription.Request request) {

        Patient validPatient = userCommonService.findPatient(user);

        checkRecordSaveLimit(validPatient);

        MultipartFile recordFile = request.getFile();

        if (recordFile == null) {
            throw new CustomErrorException(ErrorType.RecordFileNotFound);
        }
        try {
            AudioFileSaveDto audioFileSaveDto = fileService.saveAudioFile(recordFile);

            WhisperTranscription.Response transcription = openAIClientService.createTranscription(request);
            String transcriptionText = transcription.getText();

            Record newRecord = recordRepository.save(
                    Record.of(
                            validPatient,
                            audioFileSaveDto.getFileName(),
                            transcriptionText,
                            audioFileSaveDto.getAudioDuration()));

            return RecordSave.Dto.getInfo(newRecord);
        } catch (InternalErrorException e) {
            if (e.getInternalErrorType() == InternalErrorType.RecordMetaDataError) {
                throw new CustomErrorException(ErrorType.NotValidAudioFileError);
            }

            if (e.getInternalErrorType() == InternalErrorType.EmptyFileError) {
                throw new CustomErrorException(ErrorType.NoAudioFileError);
            }

            if (e.getInternalErrorType() == InternalErrorType.InvalidFileExtension) {
                throw new CustomErrorException(ErrorType.NoAudioFileError);
            }

            if (e.getInternalErrorType() == InternalErrorType.FileSaveError
                    || e.getInternalErrorType() == InternalErrorType.FileDeleteError) {
                throw new CustomErrorException(ErrorType.InternalServerError);
            }
        }

        throw new CustomErrorException(ErrorType.InternalServerError);
    }

    public RecordInquiry.Dto getAllPatientsRecords(User user) {

        Protector validProtector = userCommonService.findProtector(user);

        List<Patient> foundPatients = getPatientsByProtectorId(validProtector);

        List<Record> foundAllRecords = getAllPatientsRecords(foundPatients);

        return RecordInquiry.Dto.fromEntity(foundAllRecords, audioApiUrlPrefix, imageApiUrl);

    }

    public RecordInquiry.Dto getAllRecordsByPatientId(User user, Long patientId) {

        Protector validProtector = userCommonService.findProtector(user);

        Patient foundPatient = userCommonService.findPatient(patientId);

        userCommonService.checkUserIsProtectorOfPatient(validProtector, foundPatient);

        List<Record> foundAllRecords = getOnePatientRecords(patientId);

        return RecordInquiry.Dto.fromEntity(foundAllRecords, audioApiUrlPrefix, imageApiUrl);

    }

    public RecordDelete.Dto deleteRecord(User user, Long recordId) {

        Protector validProtector = userCommonService.findProtector(user);

        Record foundRecord = findRecord(recordId);
        String foundRecordFileName = foundRecord.getFilename();

        userCommonService.checkUserIsProtectorOfPatient(validProtector, foundRecord.getPatient());

        recordRepository.delete(foundRecord);

        try {
            fileService.deleteAudioFile(foundRecordFileName);
        } catch (Exception e) {
            throw new CustomErrorException(ErrorType.InternalServerError);
        }


        return RecordDelete.Dto.fromEntity(foundRecordFileName);
    }

    public RecordSaveLimit.Dto getRecordSaveLimit(User user) {

        Patient validPatient = userCommonService.findPatient(user);

        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay(); // 자정 (00:00)
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX); // 23:59:59

        int dailyRecordingLimit = 10;
        int todayRecordCount = recordRepository.countByPatientIdAndCreatedAtBetween(validPatient.getId(), startOfDay, endOfDay);
        int remainingRecordingCount = dailyRecordingLimit - todayRecordCount;

        return RecordSaveLimit.Dto.fromEntity(dailyRecordingLimit, remainingRecordingCount);

    }

    private Record findRecord(Long recordId) {
        return recordRepository.findById(recordId)
                .orElseThrow(() -> new CustomErrorException(ErrorType.RecordInfoNotFound));
    }


    private List<Patient> getPatientsByProtectorId(Protector validProtector) {
        List<Patient> foundPatients = patientRepository.findByProtector_id(validProtector.getId());
        if (foundPatients == null || foundPatients.isEmpty()) {
            throw new CustomErrorException(ErrorType.UserNotFoundError);
        }
        return foundPatients;
    }

    private List<Record> getAllPatientsRecords(List<Patient> foundPatients) {

        List<Long> foundPatientIds = foundPatients.stream().map(Patient::getId).collect(Collectors.toList());

        List<Record> foundAllRecords = recordRepository.findAllByPatientIdsOrderByCreatedAtDesc(foundPatientIds);

        if (foundAllRecords.isEmpty()) {
            throw new CustomErrorException(ErrorType.UserRecordInfoNotFoundError);
        }

        return foundAllRecords;
    }

    private List<Record> getOnePatientRecords(Long patientId) {
        List<Record> foundAllRecords = recordRepository.findByPatientIdOrderByCreatedAtDesc(patientId);

        if (foundAllRecords.isEmpty()) {
            throw new CustomErrorException(ErrorType.UserRecordInfoNotFoundError);
        }

        return foundAllRecords;
    }

    private void checkRecordSaveLimit(Patient validPatient) {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay(); // 자정 (00:00)
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX); // 23:59:59

        int todayRecordCount = recordRepository.countByPatientIdAndCreatedAtBetween(validPatient.getId(), startOfDay, endOfDay) + 1;


        if (todayRecordCount > 10) {
            throw new CustomErrorException(ErrorType.RecordLimitExceeded);
        }
    }

}
