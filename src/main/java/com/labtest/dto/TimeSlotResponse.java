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
public class TimeSlotResponse {
    
    private Long id;
    private Long laboratoryId;
    private String laboratoryName;
    private String laboratoryCity;
    private LocalDateTime slotTime;
    private Integer totalCapacity;
    private Integer remainingCapacity;
    private Integer bookedCount;
}
