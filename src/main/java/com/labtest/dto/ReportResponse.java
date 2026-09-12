package com.labtest.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReportResponse {
    
    private Long id;
    private Long sampleId;
    private String patientName;
    private String labTestName;
    private String results;
    private String findings;
    private String recommendations;
    private LocalDateTime reportedAt;
}
