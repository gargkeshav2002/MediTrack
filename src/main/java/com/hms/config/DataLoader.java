//package com.hms.config;
//
//import com.hms.entity.*;
//import com.hms.repository.*;
//import lombok.RequiredArgsConstructor;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//
//@Component
//@RequiredArgsConstructor
//public class DataLoader implements CommandLineRunner {
//
//    private final PatientRepository patientRepository;
//    private final DoctorRepository doctorRepository;
//    private final AppointmentRepository appointmentRepository;
//
//    @Override
//    public void run(String... args) {
//        if (doctorRepository.count() == 0) {
//            Doctor d1 = doctorRepository.save(Doctor.builder()
//                    .firstName("John")
//                    .lastName("Doe")
//                    .specialization("Cardiologist")
//                    .phone("9998887771")
//                    .email("john.doe@hms.com")
//                    .availability(true)
//                    .build());
//
//            Patient p1 = patientRepository.save(Patient.builder()
//                    .firstName("Alice")
//                    .lastName("Smith")
//                    .dob(LocalDate.of(1990, 5, 20))
//                    .gender("Female")
//                    .phone("8887776661")
//                    .email("alice.smith@mail.com")
//                    .build());
//
//            appointmentRepository.save(Appointment.builder()
//                    .doctor(d1)
//                    .patient(p1)
//                    .appointmentDate(LocalDateTime.now().plusDays(2))
//                    .status("BOOKED")
//                    .notes("First visit")
//                    .build());
//        }
//    }
//}
