package com.hms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hms.dto.AppointmentDTO;
import com.hms.entity.AppointmentStatus;
import com.hms.security.JwtFilter;
import com.hms.security.JwtUtil;
import com.hms.service.AppointmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AppointmentController.class)
@AutoConfigureMockMvc(addFilters = false)
class AppointmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtFilter jwtFilter;

    @MockBean
    private JwtUtil jwtUtil;


    @MockBean
    private AppointmentService appointmentService;

    private AppointmentDTO appointmentDTO;

    @BeforeEach
    void setUp() {
        appointmentDTO = AppointmentDTO.builder()
                .appointmentId(1L)
                .patientId(1L)
                .doctorId(2L)
                .appointmentDate(LocalDateTime.now().plusDays(1))
                .status(AppointmentStatus.BOOKED.name())
                .build();
    }

    @Test
    void bookAppointment_shouldReturnBookedAppointment() throws Exception {
        when(appointmentService.bookAppointment(any(AppointmentDTO.class)))
                .thenReturn(appointmentDTO);

        mockMvc.perform(post("/api/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(appointmentDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.appointmentId").value(1))
                .andExpect(jsonPath("$.status").value("BOOKED"));
    }

    @Test
    void cancelAppointment_shouldReturnCancelledAppointment() throws Exception {
        AppointmentDTO cancelled = AppointmentDTO.builder()
                .appointmentId(1L)
                .status(AppointmentStatus.CANCELLED.name())
                .build();

        when(appointmentService.cancelAppointment(1L)).thenReturn(cancelled);

        mockMvc.perform(put("/api/appointments/1/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    void getAppointmentsForPatient_shouldReturnPagedAppointments() throws Exception {
        Page<AppointmentDTO> page = new PageImpl<>(Arrays.asList(appointmentDTO));
        when(appointmentService.getAppointmentsForPatient(eq(1L), any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/api/appointments/patient/1")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].appointmentId").value(1));
    }

    @Test
    void getAppointmentsForDoctor_shouldReturnPagedAppointments() throws Exception {
        Page<AppointmentDTO> page = new PageImpl<>(Arrays.asList(appointmentDTO));
        when(appointmentService.getAppointmentsForDoctor(eq(2L), any(), any(), any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/api/appointments/doctor/2")
                        .param("start", LocalDateTime.now().plusDays(1).toString())
                        .param("end", LocalDateTime.now().plusDays(2).toString())
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].appointmentId").value(1));
    }
}
