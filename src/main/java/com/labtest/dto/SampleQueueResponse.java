package com.labtest.dto;

import com.labtest.entity.SampleState;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SampleQueueResponse {
    
    private Long sampleId;
    private Long bookingId;
    private String patientName;
    private String patientUsername;
    private String labTestName;
    private SampleState state;
    private LocalDateTime createdAt;
    private LocalDateTime collectedAt;
    private LocalDateTime testStartedAt;
    private LocalDateTime completedAt;
    private LocalDateTime reportedAt;
}
