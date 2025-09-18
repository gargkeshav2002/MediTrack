package com.hms.service;


import com.hms.repository.DoctorRepository;
import com.hms.repository.PatientRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class PatientTests {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Test
    public void findById(){
//        var patient = patientRepository.findById(1L);
//        assert(patient.isPresent());

        var doctor = doctorRepository.findById(4L);
        assert(doctor.isPresent());
    }
}
