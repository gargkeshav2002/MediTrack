package com.hms.mapper;

import com.hms.dto.PatientDTO;
import com.hms.entity.Patient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PatientMapper {

    public PatientDTO toPatientDTO(Patient patient) {
        if (patient == null) {
            return null;
        }
        return PatientDTO.builder()
                .patientId(patient.getPatientId())
                .firstName(patient.getFirstName())
                .lastName(patient.getLastName())
                .dob(patient.getDob())
                .gender(patient.getGender())
                .phone(patient.getPhone())
                .email(patient.getEmail())
                .build();
    }

    public Patient toPatient(PatientDTO patientDTO) {
        if (patientDTO == null) {
            return null;
        }
        return Patient.builder()
                .patientId(patientDTO.getPatientId())
                .firstName(patientDTO.getFirstName())
                .lastName(patientDTO.getLastName())
                .dob(patientDTO.getDob())
                .gender(patientDTO.getGender())
                .phone(patientDTO.getPhone())
                .email(patientDTO.getEmail())
                .build();
    }
}
