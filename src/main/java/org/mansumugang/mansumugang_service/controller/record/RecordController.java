package org.mansumugang.mansumugang_service.controller.record;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mansumugang.mansumugang_service.domain.user.User;
import org.mansumugang.mansumugang_service.dto.record.RecordDelete;
import org.mansumugang.mansumugang_service.dto.record.RecordInquiry;
import org.mansumugang.mansumugang_service.dto.record.RecordSave;
import org.mansumugang.mansumugang_service.service.record.RecordService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/record")
public class RecordController {

    private final RecordService recordService;

    // 1. 음성녹음 저장 API
    @PostMapping("/save")
    public ResponseEntity<RecordSave.Response> save(@AuthenticationPrincipal User user,
                                                    @RequestPart(name = "audio", required = true) MultipartFile recordFile
    ){
        RecordSave.Dto savedInfo = recordService.saveRecord(user, recordFile);

        return new ResponseEntity<>(RecordSave.Response.createNewResponse(savedInfo), HttpStatus.CREATED);
    }

    // 2. 음성녹음 조회 API(보호자의 전체 환자에 대해서)
    @GetMapping()
    public ResponseEntity<RecordInquiry.Response> getAllPatientsRecords(@AuthenticationPrincipal User user){
        RecordInquiry.Dto allPatientsRecords = recordService.getAllPatientsRecords(user);

        return ResponseEntity.ok(RecordInquiry.Response.createNewResponse(allPatientsRecords));
    }
    // 3. 음성녹음 조회 API(경로변수로 특정 보호자에 대해서)
    @GetMapping("/{patient_id}")
    public ResponseEntity<RecordInquiry.Response> getAllRecordsByPatientId(@AuthenticationPrincipal User user,
                                                                            @PathVariable("patient_id") Long patientId
    ){
        RecordInquiry.Dto onePatientsRecords = recordService.getAllRecordsByPatientId(user, patientId);

        return ResponseEntity.ok(RecordInquiry.Response.createNewResponse(onePatientsRecords));
    }

    // 4. 음성녹음 듣기 API
//    @PostMapping("/download")

    // 5. 음성녹음 삭제 API (전체 삭제, 녹음파일 고유번호로 하나만 삭제, 특정 환자 녹음파일 전체 삭제) -> 하나만 삭제 우선 구현
    @DeleteMapping("/delete/{record_id}")
    public ResponseEntity<RecordDelete.Response> deleteRecord(@AuthenticationPrincipal User user,
                                                              @PathVariable("record_id")Long recordId
    ){
        RecordDelete.Dto dto = recordService.deleteRecord(user, recordId);

        return ResponseEntity.ok(RecordDelete.Response.createNewResponse(dto));
    }
}