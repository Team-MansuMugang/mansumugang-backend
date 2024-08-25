package org.mansumugang.mansumugang_service.service.record;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mansumugang.mansumugang_service.constant.ErrorType;
import org.mansumugang.mansumugang_service.constant.FileType;
import org.mansumugang.mansumugang_service.domain.record.Record;
import org.mansumugang.mansumugang_service.domain.user.Patient;
import org.mansumugang.mansumugang_service.domain.user.Protector;
import org.mansumugang.mansumugang_service.domain.user.User;
import org.mansumugang.mansumugang_service.dto.record.*;
import org.mansumugang.mansumugang_service.exception.CustomErrorException;
import org.mansumugang.mansumugang_service.repository.PatientRepository;
import org.mansumugang.mansumugang_service.repository.RecordRepository;
import org.mansumugang.mansumugang_service.service.fileService.FileService;
import org.mansumugang.mansumugang_service.service.fileService.S3FileService;
import org.mansumugang.mansumugang_service.utils.ProfileChecker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class RecordService {

    private final RecordRepository recordRepository;
    private final PatientRepository patientRepository;

    private final FileService fileService;
    private final S3FileService s3FileService;
    private final ProfileChecker profileChecker;
    private final OpenAIClientService openAIClientService;

    @Value("${file.upload.audio.api}")
    private String audioApiUrlPrefix;

    @Transactional
    public RecordSave.Dto saveRecord(User user, Transcription.Request request){

        // 1. 음성녹음을 저장하려는 유저가 환자인지 검증
        Patient validPatient = validatePatient(user);

        // 음성 파일 저장 기능을 하루 10회로 제한. -> 초과면 예외 처리.
        checkRecordSaveLimit(validPatient);

        log.info("RecordService -> saveRecord 메서드 호출");

        MultipartFile recordFile = request.getFile();

        if (recordFile == null){
            throw new CustomErrorException(ErrorType.RecordFileNotFound);
        }
            try{

                // 음성파일 내용 텍스트로 변환 -> open AI 의 Whisper 사용.
                WhisperTranscription.Response transcription = openAIClientService.createTranscription(request);
                String transcriptionText = transcription.getText();

                String recordFileName = fileService.saveRecordFile(recordFile);

                Long recordDuration = fileService.getRecordDuration(recordFileName);

                if(profileChecker.checkActiveProfile("prod")) {
                    fileService.deleteRecordFile(recordFileName);
                    recordFileName = s3FileService.saveRecordFile(recordFile);
                }

                Record newRecord = recordRepository.save(Record.of(validPatient, recordFileName,  transcriptionText, recordDuration));

                return RecordSave.Dto.getInfo(newRecord);


            } catch (Exception e) {
                throw new CustomErrorException(ErrorType.InternalServerError);
            }
    }

    public RecordInquiry.Dto getAllPatientsRecords(User user){
        log.info("RecordService -> getAllRecords 메서드 호출");

        // 1. 음성을 조회하려는 유저가 보호자 객체인지 검증
        Protector validProtector = validateProtector(user);

        // 2. 검증된 보호자의 고유번호로 환자리스트 조회
        List<Patient> foundPatients = getPatientsByProtectorId(validProtector);
        log.info("찾은 환자 수 : {}명", foundPatients.size());

        // 3. 찾은 환자들의 녹음파일에 대한 정보들 조회
        List<Record> foundAllRecords = getAllPatientsRecords(foundPatients);

        return RecordInquiry.Dto.fromEntity(foundAllRecords, audioApiUrlPrefix);

    }

    public RecordInquiry.Dto getAllRecordsByPatientId(User user, Long patientId){

        // 1. 음성을 조회하려는 유저가 보호자 객체인지 검증
        Protector validProtector = validateProtector(user);

        // 2. 경로 변수로 받은 환자 고유번호로 환자 찾기
        Patient foundPatient = findPatient(patientId);

        // 3. 유효한 보호자 객체와 찾은 환자 간 보호자/피보호자 관계 확인
        checkUserIsProtectorOfPatient(validProtector, foundPatient);

        // 4.
        List<Record> foundAllRecords = getOnePatientRecords(patientId);

        return RecordInquiry.Dto.fromEntity(foundAllRecords, audioApiUrlPrefix);

    }

    public RecordDelete.Dto deleteRecord(User user, Long recordId){

        // 1. 음성 녹음 파일을 제거하려는 객체가 보호자 객체가 맞는지 검증
        Protector validProtector = validateProtector(user);

        // 2. 경로 변수로 받은 음성녹음 파일이 존재하는지 검증
        Record foundRecord = findRecord(recordId);
        String foundRecordFileName = foundRecord.getFilename();

        // 3. 보호자와 녹음파일의 소유자(환자) 간 관계 검증
        checkUserIsProtectorOfPatient(validProtector, foundRecord.getPatient());

        // 4. DB에 저장된 녹음파일 정보 삭제
        recordRepository.delete(foundRecord);

        // 5. 서버에서 녹음파일 삭제 진행
        try {
            if(profileChecker.checkActiveProfile("prod")) {
                s3FileService.deleteFileFromS3(foundRecordFileName, FileType.AUDIO);
            }else{
                fileService.deleteRecordFile(foundRecordFileName);
            }
        } catch (Exception e) {
            throw new CustomErrorException(ErrorType.InternalServerError);
        }



        return RecordDelete.Dto.fromEntity(foundRecordFileName);
    }


    private Patient validatePatient(User user) {
        log.info("AuthenticationPrincipal 로 받은 유저 객체가 환자 객체인지 검증 시작");
        if (user == null) {
            throw new CustomErrorException(ErrorType.UserNotFoundError);
        }

        if (user instanceof Patient) {

            log.info("환자 객체 검증 완료");
            return (Patient) user;
        }

        throw new CustomErrorException(ErrorType.AccessDeniedError);
    }

    private Protector validateProtector(User user) {
        log.info("AuthenticationPrincipal 로 받은 유저 객체가 보호자 객체인지 검증 시작");
        if (user == null) {
            throw new CustomErrorException(ErrorType.UserNotFoundError);
        }

        if (user instanceof Protector) {

            log.info("보호자 객체 검증 완료");
            return (Protector) user;
        }

        throw new CustomErrorException(ErrorType.AccessDeniedError);
    }

    public Patient findPatient(Long patientId) {
        log.info("patientId로 환자 찾기 시작, patientId={}", patientId);

        return patientRepository.findById(patientId)
                .orElseThrow(() -> {
                    log.error("userId로 찾은 환자가 존재하지 않음, userId={}", patientId);
                    return new CustomErrorException(ErrorType.UserNotFoundError);
                });
    }

    private Record findRecord(Long recordId) {
        return recordRepository.findById(recordId)
                .orElseThrow(() -> new CustomErrorException(ErrorType.RecordInfoNotFound));
    }

    private void checkUserIsProtectorOfPatient(Protector protector, Patient patient) {

        log.info("유저가 환자의 보호자인지 검증 시작");
        if(!patient.getProtector().getUsername().equals(protector.getUsername())) {
            throw new CustomErrorException(ErrorType.AccessDeniedError);
        }
        log.info("유저가 환자의 보호자인지 검증 완료");
    }

    private List<Patient> getPatientsByProtectorId(Protector validProtector) {
        List<Patient> foundPatients = patientRepository.findByProtector_id(validProtector.getId());
        if (foundPatients == null || foundPatients.isEmpty()){
            throw new CustomErrorException(ErrorType.UserNotFoundError);
        }
        return foundPatients;
    }

    private List<Record> getAllPatientsRecords(List<Patient> foundPatients) {

        List<Long> foundPatientIds = foundPatients.stream().map(Patient::getId).collect(Collectors.toList());

        List<Record> foundAllRecords = recordRepository.findAllByPatientIdsOrderByCreatedAtDesc(foundPatientIds);

        if(foundAllRecords.isEmpty()){
            throw new CustomErrorException(ErrorType.UserRecordInfoNotFoundError);
        }

        return foundAllRecords;
    }

    private List<Record> getOnePatientRecords(Long patientId) {
        List<Record> foundAllRecords = recordRepository.findByPatientIdOrderByCreatedAtDesc(patientId);

        if (foundAllRecords.isEmpty()){
            throw new CustomErrorException(ErrorType.UserRecordInfoNotFoundError);
        }

        return  foundAllRecords;
    }

    private void checkRecordSaveLimit(Patient validPatient) {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay(); // 자정 (00:00)
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX); // 23:59:59

        int todayRecordCount = recordRepository.countByPatientIdAndCreatedAtBetween(validPatient.getId(), startOfDay, endOfDay);

        log.info("횟수 : {} ", todayRecordCount);

        if (todayRecordCount > 9){
            throw new CustomErrorException(ErrorType.RecordLimitExceeded);
        }
    }

}
