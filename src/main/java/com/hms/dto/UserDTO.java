package com.hms.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    @NotBlank
    private String username;
    @NotBlank
    private String password;

    private String role; // e.g., "PATIENT", "DOCTOR", "ADMIN"

    private PatientDTO patientDTO;

    private DoctorDTO doctorDTO;
}
