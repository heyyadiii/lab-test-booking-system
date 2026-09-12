package com.labtest.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequest {
    
    @NotNull(message = "Time slot ID is required")
    private Long timeSlotId;
    
    @NotNull(message = "Lab test ID is required")
    private Long labTestId;
}
