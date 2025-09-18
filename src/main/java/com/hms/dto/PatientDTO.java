package com.hms.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientDTO {
    private Long patientId;

    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;

    @Past
    private LocalDate dob;

    @Pattern(regexp = "Male|Female|Other")
    private String gender;

    @NotBlank
    private String phone;

    @Email
    private String email;
}
