package com.labtest.controller;

import com.labtest.dto.BookingRequest;
import com.labtest.dto.BookingResponse;
import com.labtest.dto.LabTestResponse;
import com.labtest.dto.TimeSlotResponse;
import com.labtest.service.AdminService;
import com.labtest.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {
    
    @Autowired
    private BookingService bookingService;
    
    @Autowired
    private AdminService adminService;
    
    @GetMapping("/lab-tests")
    public ResponseEntity<List<LabTestResponse>> getAllLabTests() {
        List<LabTestResponse> labTests = adminService.getAllLabTests();
        return ResponseEntity.ok(labTests);
    }
    
    @GetMapping("/slots")
    public ResponseEntity<List<TimeSlotResponse>> getAvailableSlots(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam(required = false) Long laboratoryId) {
        List<TimeSlotResponse> slots;
        
        if (laboratoryId != null) {
            slots = bookingService.getAvailableTimeSlotsByLaboratory(laboratoryId, startTime, endTime);
        } else {
            slots = bookingService.getAvailableTimeSlots(startTime, endTime);
        }
        
        return ResponseEntity.ok(slots);
    }
    
    @PostMapping
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<BookingResponse> createBooking(
            @Valid @RequestBody BookingRequest request,
            Authentication authentication) {
        String username = authentication.getName();
        BookingResponse response = bookingService.createBooking(request, username);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping
    public ResponseEntity<List<BookingResponse>> getUserBookings(Authentication authentication) {
        String username = authentication.getName();
        List<BookingResponse> bookings = bookingService.getUserBookings(username);
        return ResponseEntity.ok(bookings);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<BookingResponse> getBookingById(
            @PathVariable Long id,
            Authentication authentication) {
        String username = authentication.getName();
        BookingResponse booking = bookingService.getBookingById(id, username);
        return ResponseEntity.ok(booking);
    }
}
