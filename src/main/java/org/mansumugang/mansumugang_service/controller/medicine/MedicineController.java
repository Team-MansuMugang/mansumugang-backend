package org.mansumugang.mansumugang_service.controller.medicine;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mansumugang.mansumugang_service.domain.user.User;
import org.mansumugang.mansumugang_service.dto.medicine.MedicineSchedule;
import org.mansumugang.mansumugang_service.dto.medicine.MedicineSummaryInfoDto;
import org.mansumugang.mansumugang_service.dto.medicine.medicineDelete.MedicineDeleteRequestDto;
import org.mansumugang.mansumugang_service.dto.medicine.medicineDelete.MedicineDeleteResponseDto;
import org.mansumugang.mansumugang_service.dto.medicine.medicineSave.MedicineSaveRequestDto;
import org.mansumugang.mansumugang_service.dto.medicine.medicineSave.MedicineSaveResponseDto;
import org.mansumugang.mansumugang_service.dto.medicine.medicineUpdate.MedicineUpdateRequestDto;
import org.mansumugang.mansumugang_service.dto.medicine.medicineUpdate.MedicineUpdateResponseDto;
import org.mansumugang.mansumugang_service.service.medicine.MedicineService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/medicine")
public class MedicineController {
    private final MedicineService medicineService;

    @GetMapping()
    public ResponseEntity<MedicineSchedule.Response> getMedicineByDate(@AuthenticationPrincipal User user,
                                                                  @RequestParam(required = true) String date,
                                                                  @RequestParam(required = true) Long patientId) {
        MedicineSchedule.Dto medicineByDate = medicineService.getMedicineByDate(user, patientId, date);
        return new ResponseEntity<>(MedicineSchedule.Response.fromDto(medicineByDate), HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<MedicineSaveResponseDto> saveMedicine(@AuthenticationPrincipal User user,
                                                                @Valid @RequestBody MedicineSaveRequestDto requestDto) {
        medicineService.saveMedicine(user, requestDto);
        return new ResponseEntity<>(new MedicineSaveResponseDto(), HttpStatus.CREATED);
    }

    @PatchMapping("/{medicineId}")
    public ResponseEntity<MedicineUpdateResponseDto> updateMedicine(@AuthenticationPrincipal User user,
                                                                    @PathVariable Long medicineId,
                                                                    @Valid @RequestBody MedicineUpdateRequestDto requestDto) {
        medicineService.updateMedicine(user, medicineId, requestDto);
        return new ResponseEntity<>(new MedicineUpdateResponseDto(), HttpStatus.CREATED);
    }

    @DeleteMapping("/{medicineId}")
    public ResponseEntity<MedicineDeleteResponseDto> deleteMedicine(@AuthenticationPrincipal User user,
                                                                    @PathVariable Long medicineId,
                                                                    @Valid @RequestBody MedicineDeleteRequestDto requestDto) {
        medicineService.deleteMedicine(user, medicineId, requestDto);
        return new ResponseEntity<>(new MedicineDeleteResponseDto(), HttpStatus.CREATED);
    }
}
