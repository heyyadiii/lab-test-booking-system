
package com.labtest.service;

import com.labtest.entity.Report;
import com.labtest.entity.Sample;
import com.labtest.entity.SampleState;
import com.labtest.repository.ReportRepository;
import com.labtest.repository.SampleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class TestProcessingService {
    
    private static final Logger logger = LoggerFactory.getLogger(TestProcessingService.class);
    
    @Autowired
    private SampleRepository sampleRepository;
    
    @Autowired
    private ReportRepository reportRepository;
    
    private final Random random = new Random();
    
    @Async("taskExecutor")
    @Transactional
    public void processTestAsync(Long sampleId, Integer processingMinutes) {
        logger.info("Starting async test processing for sample ID: {}", sampleId);
        
        try {
            // Validate processing time
            if (processingMinutes == null || processingMinutes <= 0) {
                logger.warn("Invalid processing time for sample ID: {}. Using default 1 minute.", sampleId);
                processingMinutes = 1;
            }
            
            // Simulate test processing with delay (using seconds for demo, in production use minutes * 60000)
            Thread.sleep(processingMinutes * 1000L);
            
            // Fetch fresh entity and check state (idempotency guard)
            Sample sample = sampleRepository.findById(sampleId).orElse(null);
            if (sample == null) {
                logger.warn("Sample not found for ID: {}", sampleId);
                return;
            }
            
            if (sample.getState() != SampleState.IN_TEST) {
                logger.warn("Sample ID: {} is not in IN_TEST state. Current state: {}. Skipping processing.", 
                           sampleId, sample.getState());
                return;
            }
            
            // Update sample to COMPLETED state
            sample.setState(SampleState.COMPLETED);
            sample.setCompletedAt(LocalDateTime.now());
            sampleRepository.save(sample);
            
            logger.info("Test processing completed for sample ID: {}", sampleId);
            
            // Generate report synchronously (already in async context)
            generateReport(sampleId);
            
        } catch (InterruptedException e) {
            logger.error("Test processing interrupted for sample ID: {}", sampleId, e);
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            logger.error("Error during test processing for sample ID: {}", sampleId, e);
        }
    }
    
    @Transactional
    public void generateReport(Long sampleId) {
        logger.info("Starting report generation for sample ID: {}", sampleId);
        
        try {
            // Simulate report generation delay
            Thread.sleep(2000); // 2 seconds
            
            // Fetch fresh entity and check state (idempotency guard)
            Sample sample = sampleRepository.findById(sampleId).orElse(null);
            if (sample == null) {
                logger.warn("Sample not found for ID: {}", sampleId);
                return;
            }
            
            if (sample.getState() != SampleState.COMPLETED) {
                logger.warn("Sample ID: {} is not in COMPLETED state. Current state: {}. Skipping report generation.", 
                           sampleId, sample.getState());
                return;
            }
            
            // Check if report already exists (duplicate prevention)
            if (reportRepository.findBySample(sample).isPresent()) {
                logger.warn("Report already exists for sample ID: {}. Skipping duplicate report generation.", sampleId);
                return;
            }
            
            // Create report with simulated results
            Report report = new Report();
            report.setSample(sample);
            report.setResults(generateSimulatedResult(sample));
            report.setFindings(generateSimulatedFindings(sample));
            report.setRecommendations(
                    "Please consult your doctor for clinical interpretation of the report."
            );
            report.setGeneratedAt(LocalDateTime.now());

            reportRepository.save(report);

            // Update sample to REPORTED state
            sample.setState(SampleState.REPORTED);
            sample.setReportedAt(LocalDateTime.now());
            sampleRepository.save(sample);
            
            logger.info("Report generated successfully for sample ID: {}", sampleId);
            
        } catch (InterruptedException e) {
            logger.error("Report generation interrupted for sample ID: {}", sampleId, e);
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            logger.error("Error during report generation for sample ID: {}", sampleId, e);
        }
    }

    private String generateSimulatedResult(Sample sample) {
        String testName = sample.getBooking().getLabTest().getName();

        if (testName.toLowerCase().contains("blood")) {
            return "Hemoglobin: 14.2 g/dL, WBC: 7,200 cells/uL, Platelets: 250,000 cells/uL";
        } else if (testName.toLowerCase().contains("x-ray")) {
            return "Chest X-Ray shows no significant abnormality in the simulated examination.";
        } else if (testName.toLowerCase().contains("mri")) {
            return "MRI examination shows no significant abnormality in the simulated examination.";
        } else if (testName.toLowerCase().contains("ct")) {
            return "CT scan shows no significant abnormality in the simulated examination.";
        }

        return "Simulated test result generated successfully.";
    }


    private String generateSimulatedFindings(Sample sample) {
        String testName = sample.getBooking().getLabTest().getName();

        if (testName.toLowerCase().contains("blood")) {
            return "Blood test parameters are within the simulated normal range.";
        } else if (testName.toLowerCase().contains("x-ray")) {
            return "No significant abnormality detected in the simulated X-Ray examination.";
        } else if (testName.toLowerCase().contains("mri")) {
            return "No significant abnormality detected in the simulated MRI examination.";
        } else if (testName.toLowerCase().contains("ct")) {
            return "No significant abnormality detected in the simulated CT scan.";
        }

        return "No significant abnormality detected in the simulated examination.";
    }
}
