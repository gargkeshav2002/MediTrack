package com.hms.mapper;

import com.hms.dto.DoctorDTO;
import com.hms.entity.Doctor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.print.Doc;

@Component
@RequiredArgsConstructor
public class DoctorMapper {

    public DoctorDTO toDoctorDTO(Doctor doctor){
        if(doctor == null){
            return null;
        }
        return DoctorDTO.builder()
                .doctorId(doctor.getDoctorId())
                .firstName(doctor.getFirstName())
                .lastName(doctor.getLastName())
                .specialization(doctor.getSpecialization())
                .phone(doctor.getPhone())
                .email(doctor.getEmail())
                .availability(doctor.isAvailability())
                .build();
    }

    public Doctor toDoctor(DoctorDTO doctorDTO){
        if(doctorDTO == null){
            return null;
        }
        return Doctor.builder()
                .doctorId(doctorDTO.getDoctorId())
                .firstName(doctorDTO.getFirstName())
                .lastName(doctorDTO.getLastName())
                .specialization(doctorDTO.getSpecialization())
                .phone(doctorDTO.getPhone())
                .email(doctorDTO.getEmail())
                .availability(doctorDTO.isAvailability())
                .build();
    }
}
