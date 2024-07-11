package org.mansumugang.mansumugang_service.controller.user;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.mansumugang.mansumugang_service.dto.user.location.PatientLocationDto;
import org.mansumugang.mansumugang_service.dto.user.location.PatientLocationRequestDto;
import org.mansumugang.mansumugang_service.dto.user.location.PatientLocationResponseDto;
import org.mansumugang.mansumugang_service.service.user.UserLocationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user")
public class PatientLocationController {

    private final UserLocationService userLocationService;

    // 환자 현재 위치 정보를 프론트에 전달
    @PostMapping("/location/{userId}")
    public ResponseEntity<PatientLocationResponseDto> updateUserLocation(
            @PathVariable(name = "userId") Long userid,
            @RequestBody PatientLocationRequestDto patientLocationRequestDto
    ){
        PatientLocationDto patientLocationDto = userLocationService.saveUserLocation(userid, patientLocationRequestDto);


        return new ResponseEntity<>(PatientLocationResponseDto.DtoToResponse(patientLocationDto), HttpStatus.CREATED);
    }
}
