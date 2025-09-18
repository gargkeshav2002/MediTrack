package com.hms.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorDTO {
    private Long doctorId;

    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;

    @NotBlank
    private String specialization;

    @NotBlank
    private String phone;

    @Email
    private String email;

    private boolean availability;
}
