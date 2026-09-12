package com.labtest.service;

import com.labtest.dto.BookingRequest;
import com.labtest.dto.BookingResponse;
import com.labtest.dto.TimeSlotResponse;
import com.labtest.entity.*;
import com.labtest.exception.ConflictException;
import com.labtest.exception.ResourceNotFoundException;
import com.labtest.exception.ValidationException;
import com.labtest.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingService {
    
    @Autowired
    private BookingRepository bookingRepository;
    
    @Autowired
    private TimeSlotRepository timeSlotRepository;
    
    @Autowired
    private LabTestRepository labTestRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private SampleRepository sampleRepository;
    
    public List<TimeSlotResponse> getAvailableTimeSlots(LocalDateTime startTime, LocalDateTime endTime) {
        List<TimeSlot> slots = timeSlotRepository
                .findBySlotTimeBetweenAndRemainingCapacityGreaterThan(startTime, endTime, 0);
        
        return slots.stream()
                .map(this::mapToTimeSlotResponse)
                .collect(Collectors.toList());
    }
    
    public List<TimeSlotResponse> getAvailableTimeSlotsByLaboratory(
            Long laboratoryId, LocalDateTime startTime, LocalDateTime endTime) {
        List<TimeSlot> slots = timeSlotRepository
                .findByLaboratoryIdAndSlotTimeBetweenAndRemainingCapacityGreaterThan(
                    laboratoryId, startTime, endTime, 0);
        
        return slots.stream()
                .map(this::mapToTimeSlotResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public BookingResponse createBooking(BookingRequest request, String username) {
        // Get user
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        // Get time slot with pessimistic lock to prevent race conditions
        TimeSlot timeSlot = timeSlotRepository.findById(request.getTimeSlotId())
                .orElseThrow(() -> new ResourceNotFoundException("Time slot not found"));
        
        // Get lab test
        LabTest labTest = labTestRepository.findById(request.getLabTestId())
                .orElseThrow(() -> new ResourceNotFoundException("Lab test not found"));
        
        // Check capacity
        if (timeSlot.getRemainingCapacity() <= 0) {
            throw new ConflictException("Time slot is fully booked");
        }
        
        try {
            // Update capacity - single source of truth (only remainingCapacity)
            timeSlot.setRemainingCapacity(timeSlot.getRemainingCapacity() - 1);
            timeSlotRepository.save(timeSlot);
            
            // Create booking
            Booking booking = new Booking();
            booking.setUser(user);
            booking.setTimeSlot(timeSlot);
            booking.setLabTest(labTest);
            
            Booking savedBooking = bookingRepository.save(booking);
            
            // Create sample in same transaction with bidirectional relationship
            Sample sample = new Sample();
            sample.setBooking(savedBooking);
            sample.setState(SampleState.BOOKED);
            savedBooking.setSample(sample);
            
            sampleRepository.save(sample);
            
            return mapToBookingResponse(savedBooking, sample);
            
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new ConflictException("Time slot was just booked by another user. Please try again.");
        }
    }
    
    private TimeSlotResponse mapToTimeSlotResponse(TimeSlot timeSlot) {
        // Calculate booked count from single source of truth
        int bookedCount = timeSlot.getTotalCapacity() - timeSlot.getRemainingCapacity();
        
        Laboratory lab = timeSlot.getLaboratory();
        
        return new TimeSlotResponse(
            timeSlot.getId(),
            lab.getId(),
            lab.getName(),
            lab.getCity(),
            timeSlot.getSlotTime(),
            timeSlot.getTotalCapacity(),
            timeSlot.getRemainingCapacity(),
            bookedCount
        );
    }
    
    private BookingResponse mapToBookingResponse(Booking booking, Sample sample) {
        return new BookingResponse(
            booking.getId(),
            booking.getUser().getId(),
            booking.getUser().getUsername(),
            booking.getTimeSlot().getId(),
            booking.getTimeSlot().getSlotTime(),
            booking.getLabTest().getId(),
            booking.getLabTest().getName(),
            sample != null ? sample.getId() : null,
            sample != null ? sample.getState().toString() : "PENDING",
            booking.getCreatedAt()
        );
    }
    
    public List<BookingResponse> getUserBookings(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        List<Booking> bookings = bookingRepository.findByUserOrderByCreatedAtDesc(user);
        
        return bookings.stream()
                .map(booking -> mapToBookingResponse(booking, booking.getSample()))
                .collect(Collectors.toList());
    }
    
    public BookingResponse getBookingById(Long bookingId, String username) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        
        // Verify ownership - patients can only access their own bookings
        if (!booking.getUser().getUsername().equals(username)) {
            throw new com.labtest.exception.ForbiddenException(
                "You are not authorized to access this booking"
            );
        }
        
        return mapToBookingResponse(booking, booking.getSample());
    }
}
