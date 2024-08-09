package org.mansumugang.mansumugang_service.controller.hospital;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mansumugang.mansumugang_service.domain.user.User;
import org.mansumugang.mansumugang_service.dto.hospital.HospitalDelete;
import org.mansumugang.mansumugang_service.dto.hospital.HospitalDetailGet;
import org.mansumugang.mansumugang_service.dto.hospital.HospitalSave;
import org.mansumugang.mansumugang_service.dto.hospital.HospitalUpdate;
import org.mansumugang.mansumugang_service.service.hospital.HospitalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/hospital")
public class HospitalController {
    private final HospitalService hospitalService;

    @GetMapping("/{hospitalId}")
    public ResponseEntity<HospitalDetailGet.Response> getHospitalDetail(@AuthenticationPrincipal User user,
                                                                        @PathVariable Long hospitalId) {
        HospitalDetailGet.Dto dto = hospitalService.getHospitalDetail(user, hospitalId);
        return new ResponseEntity<>(HospitalDetailGet.Response.fromDto(dto), HttpStatus.OK);
    }


    @PostMapping()
    public ResponseEntity<HospitalSave.Response> saveHospital(@AuthenticationPrincipal User user,
                                                              @Valid @RequestBody HospitalSave.Request requestDto) {
        hospitalService.saveHospital(user, requestDto);
        return new ResponseEntity<>(HospitalSave.Response.createNewResponse(), HttpStatus.CREATED);
    }

    @PatchMapping("/{hospitalId}")
    public ResponseEntity<HospitalUpdate.Response> updateHospital(@AuthenticationPrincipal User user,
                                                                  @PathVariable Long hospitalId,
                                                                  @Valid @RequestBody HospitalUpdate.Request requestDto) {
        hospitalService.updateHospital(user, hospitalId, requestDto);
        return new ResponseEntity<>(HospitalUpdate.Response.createNewResponse(), HttpStatus.CREATED);
    }

    @DeleteMapping("/{hospitalId}")
    public ResponseEntity<HospitalDelete.Response> updateHospital(@AuthenticationPrincipal User user,
                                                                  @PathVariable Long hospitalId) {
        hospitalService.deleteHospital(user, hospitalId);
        return new ResponseEntity<>(HospitalDelete.Response.createNewResponse(), HttpStatus.CREATED);
    }
}
