package com.hms.service;

import com.hms.dto.AppointmentDTO;
import com.hms.entity.*;
import com.hms.mapper.AppointmentMapper;
import com.hms.repository.AppointmentRepository;
import com.hms.repository.DoctorRepository;
import com.hms.repository.PatientRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;
    @Mock
    private PatientRepository patientRepository;
    @Mock
    private DoctorRepository doctorRepository;
    @Mock
    private AppointmentMapper appointmentMapper;
    @Mock
    private DoctorScheduleService doctorScheduleService;
    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private AppointmentService appointmentService;

    private Patient patient;
    private Doctor doctor;
    private Appointment appointment;
    private AppointmentDTO appointmentDTO;

    @BeforeEach
    void setup() {
        patient = Patient.builder()
                .patientId(1L)
                .firstName("John")
                .lastName("Doe")
                .phone("9999999999")
                .build();

        doctor = Doctor.builder()
                .doctorId(1L)
                .firstName("Alice")
                .lastName("Smith")
                .build();

        appointment = Appointment.builder()
                .appointmentId(100L)
                .appointmentDate(LocalDateTime.now().plusDays(1))
                .patient(patient)
                .doctor(doctor)
                .status(AppointmentStatus.BOOKED)
                .build();

        appointmentDTO = AppointmentDTO.builder()
                .patientId(1L)
                .doctorId(1L)
                .appointmentDate(appointment.getAppointmentDate())
                .status("BOOKED")
                .build();
    }

    // ---------- TESTS ----------
    @Test
    void bookAppointment_Success() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(doctorRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(doctor));
        when(appointmentRepository.findByDoctorAndAppointmentDate(any(), any())).thenReturn(List.of());
        when(doctorScheduleService.isDoctorAvailable(eq(1L), any(), any())).thenReturn(true);
        when(appointmentMapper.toAppointmentAppointment(any(), eq(patient), eq(doctor))).thenReturn(appointment);
        when(appointmentMapper.toAppointmentDTO(any())).thenReturn(appointmentDTO);

        AppointmentDTO result = appointmentService.bookAppointment(appointmentDTO);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo("BOOKED");
        verify(notificationService).sendSms(eq(patient.getPhone()), contains("BOOKED"));
        verify(appointmentRepository).save(any(Appointment.class));
    }
    @Test
    void bookAppointment_DoctorAlreadyBooked() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(doctorRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(doctor));
        when(appointmentRepository.findByDoctorAndAppointmentDate(any(), any()))
                .thenReturn(List.of(new Appointment()));

        assertThatThrownBy(() -> appointmentService.bookAppointment(appointmentDTO))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Doctor is not available");
    }

    @Test
    void bookAppointment_DoctorNotScheduled() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(doctorRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(doctor));
        when(appointmentRepository.findByDoctorAndAppointmentDate(any(), any())).thenReturn(List.of());
        when(doctorScheduleService.isDoctorAvailable(eq(1L), any(), any())).thenReturn(false);

        assertThatThrownBy(() -> appointmentService.bookAppointment(appointmentDTO))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("not scheduled");
    }

    @Test
    void bookAppointment_PatientNotFound() {
        when(patientRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> appointmentService.bookAppointment(appointmentDTO))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Patient not found");
    }

    @Test
    void bookAppointment_DoctorNotFound() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(doctorRepository.findByIdForUpdate(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> appointmentService.bookAppointment(appointmentDTO))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Doctor not found");
    }

    @Test
    void cancelAppointment_Success() {
        appointment.setStatus(AppointmentStatus.BOOKED);
        when(appointmentRepository.findById(100L)).thenReturn(Optional.of(appointment));
        when(appointmentMapper.toAppointmentDTO(any())).thenReturn(appointmentDTO);

        AppointmentDTO result = appointmentService.cancelAppointment(100L);

        assertThat(result).isNotNull();
        assertThat(appointment.getStatus()).isEqualTo(AppointmentStatus.CANCELLED);
        verify(notificationService).sendSms(eq(patient.getPhone()), contains("CANCELLED"));
    }

    @Test
    void cancelAppointment_NotFound() {
        when(appointmentRepository.findById(100L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> appointmentService.cancelAppointment(100L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Appointment not found");
    }

    @Test
    void completeAppointment_Success() {
        appointment.setStatus(AppointmentStatus.BOOKED);
        when(appointmentRepository.findById(100L)).thenReturn(Optional.of(appointment));
        when(appointmentMapper.toAppointmentDTO(any())).thenReturn(appointmentDTO);

        AppointmentDTO result = appointmentService.completeAppointment(100L);

        assertThat(result).isNotNull();
        assertThat(appointment.getStatus()).isEqualTo(AppointmentStatus.COMPLETED);
        verify(notificationService).sendSms(eq(patient.getPhone()), contains("COMPLETED"));
    }

    @Test
    void completeAppointment_NotFound() {
        when(appointmentRepository.findById(100L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> appointmentService.completeAppointment(100L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Appointment not found");
    }

    @Test
    void getAppointmentsForPatient_Success() {
        Page<Appointment> page = new PageImpl<>(List.of(appointment));
        Pageable pageable = PageRequest.of(0, 5);

        when(appointmentRepository.findByPatientPatientIdAndStatusNot(eq(1L), eq(AppointmentStatus.CANCELLED), eq(pageable)))
                .thenReturn(page);
        when(appointmentMapper.toAppointmentDTO(any())).thenReturn(appointmentDTO);

        Page<AppointmentDTO> result = appointmentService.getAppointmentsForPatient(1L, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getPatientId()).isEqualTo(1L);
    }

    @Test
    void getAppointmentsForDoctor_Success() {
        Page<Appointment> page = new PageImpl<>(List.of(appointment));
        Pageable pageable = PageRequest.of(0, 5);
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        when(appointmentRepository.findByDoctorDoctorIdAndAppointmentDateBetweenAndStatusNot(
                eq(1L), eq(start), eq(end), eq(AppointmentStatus.CANCELLED), eq(pageable)))
                .thenReturn(page);
        when(appointmentMapper.toAppointmentDTO(any())).thenReturn(appointmentDTO);

        Page<AppointmentDTO> result = appointmentService.getAppointmentsForDoctor(1L, start, end, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getDoctorId()).isEqualTo(1L);
    }


}