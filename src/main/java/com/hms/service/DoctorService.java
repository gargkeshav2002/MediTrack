package com.hms.service;

import com.hms.dto.DoctorDTO;
import com.hms.entity.Doctor;
import com.hms.mapper.DoctorMapper;
import com.hms.repository.DoctorRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final DoctorMapper doctorMapper;

    @Transactional(readOnly = true)
    public List<DoctorDTO> getAllDoctors(){
        return doctorRepository.findAll()
                .stream()
                .map(doctorMapper::toDoctorDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DoctorDTO getDoctorById(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Doctor not found with id " + id));
        return doctorMapper.toDoctorDTO(doctor);
    }

    @Transactional
    public DoctorDTO createDoctor(DoctorDTO doctorDTO){
        Doctor doctor = doctorMapper.toDoctor(doctorDTO);
        Doctor savedDoctor = doctorRepository.save(doctor);
        return doctorMapper.toDoctorDTO(savedDoctor);
    }

    @Transactional
    public DoctorDTO updateDoctor(Long id, DoctorDTO updatedDTO) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Doctor not found with id " + id));
        doctor.setFirstName(updatedDTO.getFirstName());
        doctor.setLastName(updatedDTO.getLastName());
        doctor.setSpecialization(updatedDTO.getSpecialization());
        doctor.setPhone(updatedDTO.getPhone());
        doctor.setEmail(updatedDTO.getEmail());
        doctor.setAvailability(updatedDTO.isAvailability());

        return doctorMapper.toDoctorDTO(doctorRepository.save(doctor));
    }

    @Transactional
    public void deleteDoctor(Long id) {
        if (!doctorRepository.existsById(id)) {
            throw new EntityNotFoundException("Doctor not found with id " + id);
        }
        doctorRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Page<DoctorDTO> searchDoctorsBySpecialization(String specialization, Pageable pageable) {
        return doctorRepository.findBySpecializationContainingIgnoreCase(specialization, pageable)
                .map(doctorMapper::toDoctorDTO);
    }
}
