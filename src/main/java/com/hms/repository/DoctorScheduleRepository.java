package com.hms.repository;

import com.hms.entity.DoctorSchedule;
import com.hms.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface DoctorScheduleRepository extends JpaRepository<DoctorSchedule, Long> {
    List<DoctorSchedule> findByDoctorAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(
            Doctor doctor,
            LocalDateTime start,
            LocalDateTime end
    );
}
