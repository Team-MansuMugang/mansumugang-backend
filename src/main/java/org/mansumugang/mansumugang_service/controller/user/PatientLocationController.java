package org.mansumugang.mansumugang_service.controller.user;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mansumugang.mansumugang_service.domain.user.User;
import org.mansumugang.mansumugang_service.dto.location.PatientLocation;
import org.mansumugang.mansumugang_service.dto.location.PatientLocationList;
import org.mansumugang.mansumugang_service.dto.location.PatientLocationRequestDto;
import org.mansumugang.mansumugang_service.service.location.UserLocationService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/location")
public class PatientLocationController {

    private final UserLocationService userLocationService;

    @PostMapping("/save")
    public ResponseEntity<PatientLocation.Response> saveUserLocation(
            @AuthenticationPrincipal User user,
            @RequestBody PatientLocationRequestDto patientLocationRequestDto
    ){
        PatientLocation.Dto patientLocationDto = userLocationService.saveUserLocation(user, patientLocationRequestDto);


        return new ResponseEntity<>(PatientLocation.Response.fromDto(patientLocationDto), HttpStatus.CREATED);
    }

    @GetMapping("/user/{patient_id}")
    public ResponseEntity<PatientLocation.Response> getUserLatestLocation(
            @AuthenticationPrincipal User user,
            @PathVariable("patient_id") Long patientId
    ){
        PatientLocation.Dto patientLocationDto = userLocationService.getUserLatestLocation(user, patientId);

        return new ResponseEntity<>(PatientLocation.Response.fromDto(patientLocationDto), HttpStatus.OK);
    }

    @GetMapping("/user")
    public ResponseEntity<PatientLocationList.Response> getUserLocationWithinRange(
            @AuthenticationPrincipal User user,
            @RequestParam("patient_id") Long patientId,
            @RequestParam("time") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)  LocalDateTime standardTime
    ){

        PatientLocationList.Dto userLocationWithinRange = userLocationService.getUserLocationWithinRange(user, patientId, standardTime);

        return ResponseEntity.ok(PatientLocationList.Response.fromDto(userLocationWithinRange));
    }
}
