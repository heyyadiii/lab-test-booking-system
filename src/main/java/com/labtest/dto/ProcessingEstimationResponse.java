package com.labtest.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProcessingEstimationResponse {
    
    private Long sampleId;
    private String currentState;
    private Integer estimatedMinutes;
    private Integer samplesAhead;
    private String message;
}
