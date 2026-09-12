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
public class BookingResponse {
    
    private Long id;
    private Long userId;
    private String userName;
    private Long timeSlotId;
    private LocalDateTime slotTime;
    private Long labTestId;
    private String labTestName;
    private Long sampleId;
    private String sampleState;
    private LocalDateTime createdAt;
}
