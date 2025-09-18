package com.hms.controller;

import com.hms.dto.DoctorScheduleDTO;
import com.hms.service.DoctorScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class DoctorScheduleController {

    private final DoctorScheduleService scheduleService;

    @PostMapping
    public ResponseEntity<DoctorScheduleDTO> createSchedule(@RequestBody DoctorScheduleDTO dto) {
        return ResponseEntity.ok(scheduleService.createSchedule(dto));
    }

    @GetMapping("/{doctorId}")
    public ResponseEntity<List<DoctorScheduleDTO>> getSchedules(@PathVariable Long doctorId) {
        return ResponseEntity.ok(scheduleService.getSchedulesForDoctor(doctorId));
    }

    @GetMapping("/doctor/{doctorId}/availability")
    public boolean isDoctorAvailable(@PathVariable Long doctorId,
                                     @RequestParam LocalDateTime start,
                                     @RequestParam LocalDateTime end) {
        return scheduleService.isDoctorAvailable(doctorId, start, end);
    }
}
