package com.labtest.dto;

import com.labtest.entity.SampleState;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSampleStatusRequest {
    
    @NotNull(message = "New state is required")
    private SampleState newState;
}
