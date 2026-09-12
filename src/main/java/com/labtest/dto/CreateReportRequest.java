package com.labtest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateReportRequest {
    
    @NotNull(message = "Sample ID is required")
    private Long sampleId;
    
    @NotBlank(message = "Test results are required")
    private String results;
    
    @NotBlank(message = "Findings are required")
    private String findings;
    
    private String recommendations; // Optional
}
