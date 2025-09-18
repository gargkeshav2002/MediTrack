package com.hms.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorScheduleDTO {
    private Long scheduleId;
    private Long doctorId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
