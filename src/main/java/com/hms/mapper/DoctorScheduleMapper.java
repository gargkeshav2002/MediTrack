package com.hms.mapper;

import com.hms.dto.DoctorScheduleDTO;
import com.hms.entity.Doctor;
import com.hms.entity.DoctorSchedule;
import org.springframework.stereotype.Component;

@Component
public class DoctorScheduleMapper {

    public DoctorScheduleDTO toDoctorScheduleDTO(DoctorSchedule schedule) {
        return DoctorScheduleDTO.builder()
                .scheduleId(schedule.getScheduleId())
                .doctorId(schedule.getDoctor().getDoctorId())
                .startTime(schedule.getStartTime())
                .endTime(schedule.getEndTime())
                .build();
    }

    public DoctorSchedule toDoctorSchedule(DoctorScheduleDTO dto, Doctor doctor) {
        return DoctorSchedule.builder()
                .scheduleId(dto.getScheduleId())
                .doctor(doctor)
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .build();
    }
}
