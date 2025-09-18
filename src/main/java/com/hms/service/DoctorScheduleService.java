package com.hms.service;

import com.hms.dto.DoctorScheduleDTO;
import com.hms.entity.Doctor;
import com.hms.entity.DoctorSchedule;
import com.hms.mapper.DoctorScheduleMapper;
import com.hms.repository.DoctorRepository;
import com.hms.repository.DoctorScheduleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DoctorScheduleService {

    private final DoctorScheduleRepository doctorScheduleRepository;
    private final DoctorRepository doctorRepository;
    private final DoctorScheduleMapper doctorScheduleMapper;

    @Transactional
    public DoctorScheduleDTO createSchedule(DoctorScheduleDTO dto) {
        Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new EntityNotFoundException("Doctor not found"));

        DoctorSchedule schedule = doctorScheduleMapper.toDoctorSchedule(dto, doctor);
        return doctorScheduleMapper.toDoctorScheduleDTO(doctorScheduleRepository.save(schedule));
    }

    @Transactional(readOnly = true)
    public List<DoctorScheduleDTO> getSchedulesForDoctor(Long doctorId){
        return doctorScheduleRepository.findAll()
                .stream()
                .filter(schedule -> schedule.getDoctor().getDoctorId().equals(doctorId))
                .map(doctorScheduleMapper::toDoctorScheduleDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public boolean isDoctorAvailable(Long doctorId, LocalDateTime start, LocalDateTime end) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new EntityNotFoundException("Doctor not found"));

        List<DoctorSchedule> schedules = doctorScheduleRepository
                .findByDoctorAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(doctor, start, end);

        return !schedules.isEmpty();
    }
}
