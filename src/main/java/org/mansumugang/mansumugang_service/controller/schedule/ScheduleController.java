package org.mansumugang.mansumugang_service.controller.schedule;

import lombok.RequiredArgsConstructor;
import org.mansumugang.mansumugang_service.domain.user.User;
import org.mansumugang.mansumugang_service.dto.medicine.MedicineSummaryInfo;
import org.mansumugang.mansumugang_service.service.schedule.ScheduleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/schedule")
public class ScheduleController {
    private final ScheduleService scheduleService;

    @GetMapping("/protector")
    public ResponseEntity<MedicineSummaryInfo.Response> getPatientScheduleByDate(@AuthenticationPrincipal User user,
                                                                                 @RequestParam String date,
                                                                                 @RequestParam Long patientId) {
        MedicineSummaryInfo.Dto medicineByDate = scheduleService.getPatientScheduleByDate(user, patientId, date);
        return new ResponseEntity<>(MedicineSummaryInfo.Response.fromDto(medicineByDate), HttpStatus.OK);
    }

    @GetMapping("/patient")
    public ResponseEntity<MedicineSummaryInfo.Response> getPatientScheduleByDate(@AuthenticationPrincipal User user,
                                                                                 @RequestParam String date) {
        MedicineSummaryInfo.Dto medicineByDate = scheduleService.getPatientScheduleByDate(user, date);
        return new ResponseEntity<>(MedicineSummaryInfo.Response.fromDto(medicineByDate), HttpStatus.OK);
    }
}
