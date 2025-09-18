package com.hms.service;

import com.hms.dto.AppointmentDTO;
import com.hms.entity.*;
import com.hms.mapper.AppointmentMapper;
import com.hms.repository.AppointmentRepository;
import com.hms.repository.DoctorRepository;
import com.hms.repository.PatientRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentMapper appointmentMapper;
    private final DoctorScheduleService doctorScheduleService;
    private final NotificationService notificationService;

    @Transactional
    public AppointmentDTO bookAppointment(AppointmentDTO appointmentDTO) {
        Patient patient = patientRepository.findById(appointmentDTO.getPatientId())
                .orElseThrow(() -> new EntityNotFoundException("Patient not found with id " + appointmentDTO.getPatientId()));

        Doctor doctor = doctorRepository.findByIdForUpdate(appointmentDTO.getDoctorId())
                .orElseThrow(() -> new EntityNotFoundException("Doctor not found with id " + appointmentDTO.getDoctorId()));

        //check Availability of doctor
        List<Appointment> existing = appointmentRepository.findByDoctorAndAppointmentDate(doctor, appointmentDTO.getAppointmentDate());
        if (!existing.isEmpty()) {
            throw new IllegalStateException("Doctor is not available at the selected time");
        }

        boolean available = doctorScheduleService.isDoctorAvailable(
                appointmentDTO.getDoctorId(),
                appointmentDTO.getAppointmentDate(),
                appointmentDTO.getAppointmentDate()
        );

        if (!available) {
            throw new IllegalStateException("Doctor is not scheduled at this time");
        }

        appointmentDTO.setStatus("BOOKED");

        Appointment appointment = appointmentMapper.toAppointmentAppointment(appointmentDTO, patient, doctor);
        appointment.setStatus(AppointmentStatus.BOOKED);

        appointmentRepository.save(appointment);

        // Send SMS
        String msg = "Hi " + patient.getFirstName() +
                ", your appointment with Dr. " + doctor.getLastName() +
                " on " + appointment.getAppointmentDate() + " is BOOKED.";
        notificationService.sendSms(patient.getPhone(), msg);

        return appointmentMapper.toAppointmentDTO(appointment);
    }

    @Transactional
    public AppointmentDTO cancelAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new EntityNotFoundException("Appointment not found"));

        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);

        // Send SMS
        String msg = "Hi " + appointment.getPatient().getFirstName() + " " + appointment.getPatient().getLastName() +
                ", your appointment with Dr. " + appointment.getDoctor().getLastName() +
                " on " + appointment.getAppointmentDate() + " is CANCELLED.";
        notificationService.sendSms(appointment.getPatient().getPhone(), msg);

        return appointmentMapper.toAppointmentDTO(appointment);
    }

    @Transactional
    public AppointmentDTO completeAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new EntityNotFoundException("Appointment not found"));

        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointmentRepository.save(appointment);

        // Send SMS
        String msg = "Hi " + appointment.getPatient().getFirstName() + " " + appointment.getPatient().getLastName() +
                ", your appointment with Dr. " + appointment.getDoctor().getLastName() +
                " on " + appointment.getAppointmentDate() + " is COMPLETED.";
        notificationService.sendSms(appointment.getPatient().getPhone(), msg);

        return appointmentMapper.toAppointmentDTO(appointment);
    }

    @Transactional(readOnly = true)
    public Page<AppointmentDTO> getAppointmentsForPatient(Long patientId, Pageable pageable) {
        return appointmentRepository
                .findByPatientPatientIdAndStatusNot(patientId, AppointmentStatus.CANCELLED, pageable)
                .map(appointmentMapper::toAppointmentDTO);
    }

    @Transactional(readOnly = true)
    public Page<AppointmentDTO> getAppointmentsForDoctor(Long doctorId, LocalDateTime start, LocalDateTime end, Pageable pageable) {
        return appointmentRepository
                .findByDoctorDoctorIdAndAppointmentDateBetweenAndStatusNot(doctorId, start, end, AppointmentStatus.CANCELLED, pageable)
                .map(appointmentMapper::toAppointmentDTO);
    }
}
