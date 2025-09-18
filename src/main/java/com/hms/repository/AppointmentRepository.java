package com.hms.repository;

import com.hms.entity.Appointment;
import com.hms.entity.AppointmentStatus;
import com.hms.entity.Doctor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByDoctorAndAppointmentDate(Doctor doctor, LocalDateTime appointmentDate);

    List<Appointment> findByDoctorDoctorIdAndAppointmentDateBetween(Long doctorId, LocalDateTime start, LocalDateTime end);

    Page<Appointment> findByPatientPatientIdAndStatusNot(Long patientId, AppointmentStatus status, Pageable pageable);

    Page<Appointment> findByDoctorDoctorIdAndAppointmentDateBetweenAndStatusNot(
            Long doctorId,
            LocalDateTime start,
            LocalDateTime end,
            AppointmentStatus status,
            Pageable pageable
    );
}
