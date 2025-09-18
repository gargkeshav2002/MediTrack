package com.hms.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentDTO {
    private Long appointmentId;

    @NotNull
    private Long patientId;

    @NotNull
    private Long doctorId;

    @Future
    private LocalDateTime appointmentDate;

    private String status;
    private String notes;
}
