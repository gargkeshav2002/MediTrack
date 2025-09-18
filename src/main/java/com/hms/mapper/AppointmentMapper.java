package com.hms.mapper;

import com.hms.dto.AppointmentDTO;
import com.hms.entity.Appointment;
import com.hms.entity.AppointmentStatus;
import com.hms.entity.Doctor;
import com.hms.entity.Patient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AppointmentMapper {

    public AppointmentDTO toAppointmentDTO(Appointment appointment) {
        if (appointment == null) {
            return null;
        }
        return AppointmentDTO.builder()
                .appointmentId(appointment.getAppointmentId())
                .patientId(appointment.getPatient().getPatientId())
                .doctorId(appointment.getDoctor().getDoctorId())
                .appointmentDate(appointment.getAppointmentDate())
                .status(String.valueOf(appointment.getStatus()))
                .notes(appointment.getNotes())
                .build();
    }

    public Appointment toAppointmentAppointment(AppointmentDTO dto, Patient patient, Doctor doctor) {
        return Appointment.builder()
                .appointmentId(dto.getAppointmentId())
                .patient(patient)
                .doctor(doctor)
                .appointmentDate(dto.getAppointmentDate())
                .status(AppointmentStatus.valueOf(dto.getStatus()))
                .notes(dto.getNotes())
                .build();
    }
}
