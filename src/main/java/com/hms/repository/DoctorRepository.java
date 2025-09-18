package com.hms.repository;

import com.hms.entity.Doctor;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    List<Doctor> findBySpecialization(String specialization);
    Optional<Doctor> findByEmail(String email);
    Optional<Doctor> findByPhone(String phone);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT d FROM Doctor d WHERE d.doctorId = :doctorId")
    Optional<Doctor> findByIdForUpdate(Long doctorId);

    Page<Doctor> findBySpecializationContainingIgnoreCase(String specialization, Pageable pageable);
}
