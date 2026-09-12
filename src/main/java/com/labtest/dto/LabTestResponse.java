package com.labtest.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LabTestResponse {
    
    private Long id;
    private String name;
    private String description;
    private Integer averageProcessingMinutes;
}
