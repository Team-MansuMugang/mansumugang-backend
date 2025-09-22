package org.mansumugang.mansumugang_service.controller.record;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mansumugang.mansumugang_service.domain.user.User;
import org.mansumugang.mansumugang_service.dto.record.*;
import org.mansumugang.mansumugang_service.service.record.RecordService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

    @PostMapping(value = "/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<RecordSave.Response> save(@AuthenticationPrincipal User user,
                                                    @ModelAttribute Transcription.Request request
    ){
        RecordSave.Dto savedInfo = recordService.saveRecord(user, request);

        return new ResponseEntity<>(RecordSave.Response.createNewResponse(savedInfo), HttpStatus.CREATED);
    }

    @GetMapping()
    public ResponseEntity<RecordInquiry.Response> getAllPatientsRecords(@AuthenticationPrincipal User user){
        RecordInquiry.Dto allPatientsRecords = recordService.getAllPatientsRecords(user);

        return ResponseEntity.ok(RecordInquiry.Response.createNewResponse(allPatientsRecords));
    }

    @GetMapping("/{patient_id}")
    public ResponseEntity<RecordInquiry.Response> getAllRecordsByPatientId(@AuthenticationPrincipal User user,
                                                                            @PathVariable("patient_id") Long patientId
    ){
        RecordInquiry.Dto onePatientsRecords = recordService.getAllRecordsByPatientId(user, patientId);

        return ResponseEntity.ok(RecordInquiry.Response.createNewResponse(onePatientsRecords));
    }

    @DeleteMapping("/delete/{record_id}")
    public ResponseEntity<RecordDelete.Response> deleteRecord(@AuthenticationPrincipal User user,
                                                              @PathVariable("record_id")Long recordId
    ){
        RecordDelete.Dto dto = recordService.deleteRecord(user, recordId);

        return ResponseEntity.ok(RecordDelete.Response.createNewResponse(dto));
    }

    @GetMapping("/check/saveLimit")
    public ResponseEntity<RecordSaveLimit.Response> getRecordSaveLimit(@AuthenticationPrincipal User user){

        RecordSaveLimit.Dto dto = recordService.getRecordSaveLimit(user);

        return ResponseEntity.ok(RecordSaveLimit.Response.createNewResponse(dto));
    }
}
