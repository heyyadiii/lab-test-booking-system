package com.labtest.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TimeSlotRequest {
    
    @NotNull(message = "Laboratory ID is required")
    private Long laboratoryId;
    
    @NotNull(message = "Slot time is required")
    private LocalDateTime slotTime;
    
    @NotNull(message = "Total capacity is required")
    @Min(value = 1, message = "Total capacity must be at least 1")
    private Integer totalCapacity;
}
