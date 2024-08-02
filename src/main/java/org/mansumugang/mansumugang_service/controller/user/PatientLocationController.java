package org.mansumugang.mansumugang_service.controller.user;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mansumugang.mansumugang_service.domain.user.User;
import org.mansumugang.mansumugang_service.dto.location.PatientLocationDto;
import org.mansumugang.mansumugang_service.dto.location.PatientLocationRequestDto;
import org.mansumugang.mansumugang_service.dto.location.PatientLocationResponseDto;
import org.mansumugang.mansumugang_service.service.location.UserLocationService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/location")
public class PatientLocationController {

    private final UserLocationService userLocationService;

    // 환자 현재 위치 정보 저장
    @PostMapping("/save")
    public ResponseEntity<PatientLocationResponseDto> saveUserLocation(
            @AuthenticationPrincipal User patient,
            @RequestBody PatientLocationRequestDto patientLocationRequestDto
    ){
        PatientLocationDto patientLocationDto = userLocationService.saveUserLocation(patient, patientLocationRequestDto);


        return new ResponseEntity<>(PatientLocationResponseDto.DtoToResponse(patientLocationDto), HttpStatus.CREATED);
    }

    @GetMapping("/user/{patient_id}")
    public ResponseEntity<PatientLocationResponseDto> getUserLatestLocation(
            @AuthenticationPrincipal User protector,
            @PathVariable("patient_id") Long patientId
    ){
        PatientLocationDto patientLocationDto = userLocationService.getUserLatestLocation(protector, patientId);

        return new ResponseEntity<>(PatientLocationResponseDto.DtoToResponse(patientLocationDto), HttpStatus.OK);
    }

    @GetMapping("/user")
    public ResponseEntity<List<PatientLocationResponseDto>> getUserLocationWithinRange(
            @AuthenticationPrincipal User protector,
            @RequestParam("patient_id") Long patientId,
            @RequestParam("time") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)  LocalDateTime standardTime
    ){

        List<PatientLocationResponseDto> userLocationsWithinRange = userLocationService.getUserLocationWithinRange(protector, patientId, standardTime);

        return ResponseEntity.ok(userLocationsWithinRange);
    }

}
