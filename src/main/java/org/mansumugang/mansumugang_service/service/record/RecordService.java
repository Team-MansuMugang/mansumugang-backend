package org.mansumugang.mansumugang_service.service.record;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mansumugang.mansumugang_service.constant.ErrorType;
import org.mansumugang.mansumugang_service.domain.record.Record;
import org.mansumugang.mansumugang_service.domain.user.Patient;
import org.mansumugang.mansumugang_service.domain.user.Protector;
import org.mansumugang.mansumugang_service.domain.user.User;
import org.mansumugang.mansumugang_service.dto.record.RecordInquiry;
import org.mansumugang.mansumugang_service.dto.record.RecordSave;
import org.mansumugang.mansumugang_service.exception.CustomErrorException;
import org.mansumugang.mansumugang_service.repository.PatientRepository;
import org.mansumugang.mansumugang_service.repository.RecordRepository;
import org.mansumugang.mansumugang_service.service.fileService.FileService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

    @Transactional
    public RecordSave.SavedInfo saveRecord(User user, MultipartFile recordFile){

        log.info("RecordService -> saveRecord 메서드 호출");

        // 1. 음성녹음을 저장하려는 유저가 환자인지 검증
        Patient validPatient = validatePatient(user);

        String recordFileName = null;
        Long recordDuration = null;
        String savedPath = null;
        if (recordFile != null) {
            try {
                log.info("녹음파일 저장 시작");
                recordFileName = fileService.saveRecordFile(recordFile);
                log.info("녹음파일 저장 완료");

                log.info("저장된 녹음파일 이름={}", recordFileName );

                recordDuration = fileService.getRecordDuration(recordFileName);
                log.info("저장된 녹음파일의 재생시간 : {}초", recordDuration);

                savedPath = fileService.getSavedPath(recordFileName);

            } catch (Exception e) {
                throw new CustomErrorException(ErrorType.InternalServerError);
            }

        }
        Record newRecord = recordRepository.save(Record.of(validPatient, recordFileName, recordDuration, savedPath));
        log.info("저장된 녹음파일에 대한 정보 DB에 저장 완료");

        return RecordSave.SavedInfo.getInfo(newRecord);

    }

    public List<RecordInquiry.Response> getAllPatientsRecords(User user){
        log.info("RecordService -> getAllRecords 메서드 호출");

        // 1. 음성을 조회하려는 유저가 보호자 객체인지 검증
        Protector validProtector = validateProtector(user);

        // 2. 검증된 보호자의 고유번호로 환자리스트 조회
        List<Patient> foundPatients = getPatientsByProtectorId(validProtector);
        log.info("찾은 환자 수 : {}명", foundPatients.size());

        // 3. 찾은 환자들의 녹음파일에 대한 정보들 조회
        List<Record> foundAllRecords = getAllPatientsRecords(foundPatients);

        List<RecordInquiry.Dto> recordInquiryDtos = getDtos(foundAllRecords);

        return RecordInquiry.Response.createNewResponse(recordInquiryDtos);
    }

    public List<RecordInquiry.Response> getOnePatientsRecords(User user, Long patientId){

        // 1. 음성을 조회하려는 유저가 보호자 객체인지 검증
        Protector validProtector = validateProtector(user);

        // 2. 경로 변수로 받은 환자 고유번호로 환자 찾기
        Patient foundPatient = findPatient(patientId);

        // 3. 유효한 보호자 객체와 찾은 환자 간 보호자/피보호자 관계 확인
        checkUserIsProtectorOfPatient(validProtector, foundPatient);

        // 4.
        List<Record> foundAllRecords = getOnePatientRecords(patientId);

        List<RecordInquiry.Dto> recordInquiryDtos = getDtos(foundAllRecords);

        return RecordInquiry.Response.createNewResponse(recordInquiryDtos);

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
        List<Record> foundAllRecords = new ArrayList<>();
        for (Patient patient : foundPatients) {
            List<Record> patientRecords = recordRepository.findByPatient_idOrderByCreatedAtDesc(patient.getId());
            foundAllRecords.addAll(patientRecords);
        }
        return foundAllRecords;
    }

    private List<Record> getOnePatientRecords(Long patientId) {
        return Optional.ofNullable(recordRepository.findByPatient_idOrderByCreatedAtDesc(patientId))
                .orElseThrow(() -> new CustomErrorException(ErrorType.UserRecordInfoNotFoundError));
    }

    private static List<RecordInquiry.Dto> getDtos(List<Record> foundAllRecords) {
        return foundAllRecords.stream()
                .map(record -> RecordInquiry.Dto.fromEntity(record))
                .collect(Collectors.toList());
    }
}
