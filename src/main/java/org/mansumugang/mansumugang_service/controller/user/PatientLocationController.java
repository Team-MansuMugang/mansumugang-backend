package org.mansumugang.mansumugang_service.controller.user;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.mansumugang.mansumugang_service.domain.user.User;
import org.mansumugang.mansumugang_service.dto.user.location.PatientLocationDto;
import org.mansumugang.mansumugang_service.dto.user.location.PatientLocationRequestDto;
import org.mansumugang.mansumugang_service.dto.user.location.PatientLocationResponseDto;
import org.mansumugang.mansumugang_service.service.user.UserLocationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/location")
public class PatientLocationController {

    private final UserLocationService userLocationService;

    // 환자 현재 위치 정보 저장
    @PostMapping("/save")
    public ResponseEntity<PatientLocationResponseDto> saveUserLocation(
            @AuthenticationPrincipal User user,
            @RequestBody PatientLocationRequestDto patientLocationRequestDto
    ){
        PatientLocationDto patientLocationDto = userLocationService.saveUserLocation(user, patientLocationRequestDto);


        return new ResponseEntity<>(PatientLocationResponseDto.DtoToResponse(patientLocationDto), HttpStatus.CREATED);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<PatientLocationResponseDto> getUserLocation(
            @PathVariable(name = "userId") Long userId
    ){
        PatientLocationDto patientLocationDto = userLocationService.getUserLocation(userId);

        return new ResponseEntity<>(PatientLocationResponseDto.DtoToResponse(patientLocationDto), HttpStatus.OK);
    }
}
