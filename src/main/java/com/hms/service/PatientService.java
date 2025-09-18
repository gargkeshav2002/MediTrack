package com.hms.service;

import com.hms.dto.PatientDTO;
import com.hms.entity.Patient;
import com.hms.mapper.PatientMapper;
import com.hms.repository.PatientRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;

    @Transactional(readOnly = true)
    public List<PatientDTO> getAllPatients(){
        List<PatientDTO> list = new ArrayList<>();
        for (Patient patient : patientRepository.findAll()) {
            PatientDTO patientDTO = patientMapper.toPatientDTO(patient);
            list.add(patientDTO);
        }
        return list;
    }

    @Transactional(readOnly = true)
    public PatientDTO getPatientById(Long id){
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        return patientMapper.toPatientDTO(patient);
    }

    @Transactional
    public PatientDTO createPatient(PatientDTO patientDTO){
        Patient patient = patientMapper.toPatient(patientDTO);
        Patient savedPatient = patientRepository.save(patient);
        return patientMapper.toPatientDTO(savedPatient);
    }

    @Transactional
    public PatientDTO updatePatient(Long id, PatientDTO updatedDTO) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Patient not found with id " + id));

        patient.setFirstName(updatedDTO.getFirstName());
        patient.setLastName(updatedDTO.getLastName());
        patient.setDob(updatedDTO.getDob());
        patient.setGender(updatedDTO.getGender());
        patient.setPhone(updatedDTO.getPhone());
        patient.setEmail(updatedDTO.getEmail());

        return patientMapper.toPatientDTO(patientRepository.save(patient));
    }

    @Transactional
    public void deletePatient(Long id) {
        if (!patientRepository.existsById(id)) {
            throw new EntityNotFoundException("Patient not found with id " + id);
        }
        try {
            patientRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
