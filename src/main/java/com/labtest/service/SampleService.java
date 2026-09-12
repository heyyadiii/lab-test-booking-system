package com.labtest.service;

import com.labtest.dto.UpdateSampleStatusRequest;
import com.labtest.entity.Sample;
import com.labtest.entity.SampleState;
import com.labtest.exception.ResourceNotFoundException;
import com.labtest.repository.SampleRepository;
import com.labtest.util.SampleStateTransitionValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class SampleService {
    
    private static final Logger logger = LoggerFactory.getLogger(SampleService.class);
    
    @Autowired
    private SampleRepository sampleRepository;
    
    @Autowired
    private TestProcessingService testProcessingService;
    
    @Transactional
    public Sample updateSampleStatus(Long sampleId, UpdateSampleStatusRequest request) {
        Sample sample = sampleRepository.findById(sampleId)
                .orElseThrow(() -> new ResourceNotFoundException("Sample not found"));
        
        SampleState currentState = sample.getState();
        SampleState newState = request.getNewState();
        
        // Validate state transition
        SampleStateTransitionValidator.validateTransition(currentState, newState);
        
        // Update state and record timestamp
        sample.setState(newState);
        
        // Record specific timestamps based on state
        switch (newState) {
            case COLLECTED:
                sample.setCollectedAt(LocalDateTime.now());
                break;
            case IN_TEST:
                sample.setTestStartedAt(LocalDateTime.now());
                break;
            case COMPLETED:
                sample.setCompletedAt(LocalDateTime.now());
                break;
            case REPORTED:
                sample.setReportedAt(LocalDateTime.now());
                break;
            default:
                break;
        }
        
        Sample savedSample = sampleRepository.save(sample);
        
        // Trigger async processing when transitioning to IN_TEST
        if (newState == SampleState.IN_TEST) {
            Integer processingMinutes = sample.getBooking().getLabTest().getAverageProcessingMinutes();
            logger.info("Triggering async test processing for sample ID: {} with processing time: {} minutes", 
                       sampleId, processingMinutes);
            testProcessingService.processTestAsync(sampleId, processingMinutes);
        }
        
        return savedSample;
    }
    
    public java.util.List<Sample> getSamplesByState(SampleState state) {
        return sampleRepository.findByStateOrderByCreatedAtAsc(state);
    }

    public com.labtest.dto.ProcessingEstimationResponse getProcessingTimeEstimation(Long sampleId, String username) {
        Sample sample = sampleRepository.findById(sampleId)
                .orElseThrow(() -> new ResourceNotFoundException("Sample not found"));
        
        // Verify ownership - user can only see their own sample estimation
        if (!sample.getBooking().getUser().getUsername().equals(username)) {
            throw new com.labtest.exception.ForbiddenException(
                "You are not authorized to access this sample estimation"
            );
        }
        
        SampleState currentState = sample.getState();
        Integer averageProcessingMinutes = sample.getBooking().getLabTest().getAverageProcessingMinutes();
        
        // Handle null or invalid processing time
        if (averageProcessingMinutes == null || averageProcessingMinutes <= 0) {
            averageProcessingMinutes = 15; // Default 15 minutes
        }
        
        // If already completed or reported, no estimation needed
        if (currentState == SampleState.COMPLETED || currentState == SampleState.REPORTED) {
            return new com.labtest.dto.ProcessingEstimationResponse(
                sampleId,
                currentState.toString(),
                0,
                0,
                "Sample processing is complete"
            );
        }
        
        // If in test, calculate remaining time based on elapsed time
        if (currentState == SampleState.IN_TEST) {
            LocalDateTime testStartedAt = sample.getTestStartedAt();
            int remainingMinutes = averageProcessingMinutes;
            
            if (testStartedAt != null) {
                long elapsedMinutes = java.time.Duration.between(testStartedAt, LocalDateTime.now()).toMinutes();
                remainingMinutes = Math.max(0, averageProcessingMinutes - (int) elapsedMinutes);
            }
            
            return new com.labtest.dto.ProcessingEstimationResponse(
                sampleId,
                currentState.toString(),
                remainingMinutes,
                0,
                "Sample is currently being processed"
            );
        }
        
        // For BOOKED or COLLECTED state, calculate based on multi-stage queue
        int totalEstimatedMinutes = 0;
        int totalSamplesAhead = 0;
        
        // Count samples in IN_TEST state (they're being processed now)
        java.util.List<Sample> inTestSamples = sampleRepository
                .findByStateOrderByCreatedAtAsc(SampleState.IN_TEST);
        
        for (Sample inTestSample : inTestSamples) {
            // Only count if created before current sample
            if (inTestSample.getCreatedAt().isBefore(sample.getCreatedAt())) {
                Integer testAvgTime = inTestSample.getBooking().getLabTest().getAverageProcessingMinutes();
                if (testAvgTime != null && testAvgTime > 0) {
                    // Calculate remaining time for in-test samples
                    LocalDateTime testStartedAt = inTestSample.getTestStartedAt();
                    if (testStartedAt != null) {
                        long elapsedMinutes = java.time.Duration.between(testStartedAt, LocalDateTime.now()).toMinutes();
                        int remainingMinutes = Math.max(0, testAvgTime - (int) elapsedMinutes);
                        totalEstimatedMinutes += remainingMinutes;
                    } else {
                        totalEstimatedMinutes += testAvgTime;
                    }
                    totalSamplesAhead++;
                }
            }
        }
        
        // If current sample is COLLECTED, also count COLLECTED samples ahead
        if (currentState == SampleState.COLLECTED) {
            java.util.List<Sample> collectedSamples = sampleRepository
                    .findByStateAndCreatedAtBefore(SampleState.COLLECTED, sample.getCreatedAt());
            
            for (Sample collectedSample : collectedSamples) {
                Integer testAvgTime = collectedSample.getBooking().getLabTest().getAverageProcessingMinutes();
                if (testAvgTime != null && testAvgTime > 0) {
                    totalEstimatedMinutes += testAvgTime;
                } else {
                    totalEstimatedMinutes += 15; // Default
                }
                totalSamplesAhead++;
            }
        }
        
        // Add current sample's processing time
        totalEstimatedMinutes += averageProcessingMinutes;
        
        // If no samples ahead, return baseline time
        if (totalSamplesAhead == 0) {
            return new com.labtest.dto.ProcessingEstimationResponse(
                sampleId,
                currentState.toString(),
                averageProcessingMinutes,
                0,
                "Your sample is next in queue"
            );
        }
        
        return new com.labtest.dto.ProcessingEstimationResponse(
            sampleId,
            currentState.toString(),
            totalEstimatedMinutes,
            totalSamplesAhead,
            String.format("Estimated wait time: %d minutes (%d samples ahead)", 
                         totalEstimatedMinutes, totalSamplesAhead)
        );
    }
}
