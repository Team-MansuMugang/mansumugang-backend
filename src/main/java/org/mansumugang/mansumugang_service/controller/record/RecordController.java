package org.mansumugang.mansumugang_service.controller.record;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mansumugang.mansumugang_service.domain.user.User;
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
                                                    @RequestPart(name = "audio", required = false) MultipartFile recordFile
    ){
        RecordSave.SavedInfo savedInfo = recordService.saveRecord(user, recordFile);

        return new ResponseEntity<>(RecordSave.Response.createNewResponse(savedInfo), HttpStatus.CREATED);
    }

    // 2. 음성녹음 조회 API(보호자의 전체 환자에 대해서)
    @GetMapping()
    public ResponseEntity<List<RecordInquiry.Response>> getAllPatientsRecords(@AuthenticationPrincipal User user){
        List<RecordInquiry.Response> allRecords = recordService.getAllPatientsRecords(user);

        return ResponseEntity.ok(allRecords);
    }
    // 3. 음성녹음 조회 API(경로변수로 특정 보호자에 대해서)
    @GetMapping("/{patient_id}")
    public ResponseEntity<List<RecordInquiry.Response>> getAllRecordsWithId(@AuthenticationPrincipal User user,
                                                                            @PathVariable("patient_id") Long patientId
    ){
        List<RecordInquiry.Response> allRecords = recordService.getOnePatientsRecords(user, patientId);

        return ResponseEntity.ok(allRecords);
    }

    // 4. 음성녹음 듣기 API
//    @PostMapping("/download")

    // 5. 음성녹음 삭제 API
//    @PostMapping("/delete")
}
