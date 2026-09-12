package com.labtest.repository;

import com.labtest.entity.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {
    
    List<TimeSlot> findBySlotTimeBetweenAndRemainingCapacityGreaterThan(
            LocalDateTime startTime, 
            LocalDateTime endTime, 
            Integer minCapacity
    );
    
    List<TimeSlot> findByLaboratoryIdAndSlotTimeBetweenAndRemainingCapacityGreaterThan(
            Long laboratoryId,
            LocalDateTime startTime,
            LocalDateTime endTime,
            Integer minCapacity
    );
}
