package com.labtest.controller;

import com.labtest.dto.LabTestRequest;
import com.labtest.dto.LabTestResponse;
import com.labtest.dto.TimeSlotRequest;
import com.labtest.dto.TimeSlotResponse;
import com.labtest.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    
    @Autowired
    private AdminService adminService;
    
    @PostMapping("/time-slots")
    public ResponseEntity<TimeSlotResponse> createTimeSlot(@Valid @RequestBody TimeSlotRequest request) {
        TimeSlotResponse response = adminService.createTimeSlot(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PutMapping("/time-slots/{id}")
    public ResponseEntity<TimeSlotResponse> updateTimeSlotCapacity(
            @PathVariable Long id,
            @RequestParam Integer capacity) {
        TimeSlotResponse response = adminService.updateTimeSlotCapacity(id, capacity);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/lab-tests")
    public ResponseEntity<LabTestResponse> createLabTest(@Valid @RequestBody LabTestRequest request) {
        LabTestResponse response = adminService.createLabTest(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/lab-tests")
    @PreAuthorize("isAuthenticated()") // Allow all authenticated users
    public ResponseEntity<List<LabTestResponse>> getAllLabTests() {
        List<LabTestResponse> response = adminService.getAllLabTests();
        return ResponseEntity.ok(response);
    }
}
