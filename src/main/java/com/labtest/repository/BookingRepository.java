package com.labtest.repository;

import com.labtest.entity.Booking;
import com.labtest.entity.TimeSlot;
import com.labtest.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    
    List<Booking> findByUserOrderByCreatedAtDesc(User user);
    
    Integer countByTimeSlot(TimeSlot timeSlot);
}
