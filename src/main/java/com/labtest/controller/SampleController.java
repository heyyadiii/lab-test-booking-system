package com.labtest.controller;

import com.labtest.dto.SampleQueueResponse;
import com.labtest.dto.UpdateSampleStatusRequest;
import com.labtest.entity.Sample;
import com.labtest.entity.SampleState;
import com.labtest.service.SampleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/samples")
public class SampleController {
    
    @Autowired
    private SampleService sampleService;
    
    @GetMapping("/queue/{state}")
    @PreAuthorize("hasRole('LAB_TECHNICIAN') or hasRole('ADMIN')")
    public ResponseEntity<List<SampleQueueResponse>> getSamplesByState(@PathVariable SampleState state) {
        List<Sample> samples = sampleService.getSamplesByState(state);
        
        List<SampleQueueResponse> response = samples.stream()
                .map(this::mapToQueueResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('LAB_TECHNICIAN') or hasRole('ADMIN')")
    public ResponseEntity<SampleQueueResponse> updateSampleStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateSampleStatusRequest request) {
        
        Sample sample = sampleService.updateSampleStatus(id, request);
        
        return ResponseEntity.ok(mapToQueueResponse(sample));
    }
    
    private SampleQueueResponse mapToQueueResponse(Sample sample) {
        return new SampleQueueResponse(
            sample.getId(),
            sample.getBooking().getId(),
            sample.getBooking().getUser().getFullName(),
            sample.getBooking().getUser().getUsername(),
            sample.getBooking().getLabTest().getName(),
            sample.getState(),
            sample.getCreatedAt(),
            sample.getCollectedAt(),
            sample.getTestStartedAt(),
            sample.getCompletedAt(),
            sample.getReportedAt()
        );
    }
    
    @GetMapping("/{id}/estimation")
    public ResponseEntity<com.labtest.dto.ProcessingEstimationResponse> getProcessingEstimation(
            @PathVariable Long id,
            org.springframework.security.core.Authentication authentication) {
        String username = authentication.getName();
        com.labtest.dto.ProcessingEstimationResponse estimation = 
            sampleService.getProcessingTimeEstimation(id, username);
        return ResponseEntity.ok(estimation);
    }
}
