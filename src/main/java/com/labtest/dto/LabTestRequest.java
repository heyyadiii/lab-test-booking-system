package com.labtest.dto;

import jakarta.validation.constraints.Min;
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
public class LabTestRequest {
    
    @NotBlank(message = "Lab test name is required")
    private String name;
    
    private String description;
    
    @NotNull(message = "Average processing time is required")
    @Min(value = 1, message = "Average processing time must be at least 1 minute")
    private Integer averageProcessingMinutes;
}
