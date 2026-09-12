package com.labtest.util;

import com.labtest.entity.SampleState;
import com.labtest.exception.InvalidStateTransitionException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SampleStateTransitionValidator {
    
    private static final Map<SampleState, List<SampleState>> VALID_TRANSITIONS = new HashMap<>();
    
    static {
        // Define valid state transitions
        VALID_TRANSITIONS.put(SampleState.BOOKED, List.of(SampleState.COLLECTED));
        VALID_TRANSITIONS.put(SampleState.COLLECTED, List.of(SampleState.IN_TEST));
        VALID_TRANSITIONS.put(SampleState.IN_TEST, List.of(SampleState.COMPLETED));
        VALID_TRANSITIONS.put(SampleState.COMPLETED, List.of(SampleState.REPORTED));
        VALID_TRANSITIONS.put(SampleState.REPORTED, List.of()); // Terminal state
    }
    
    public static void validateTransition(SampleState currentState, SampleState newState) {
        if (currentState == newState) {
            throw new InvalidStateTransitionException(
                "Sample is already in " + currentState + " state"
            );
        }
        
        List<SampleState> allowedStates = VALID_TRANSITIONS.get(currentState);
        
        if (allowedStates == null || !allowedStates.contains(newState)) {
            throw new InvalidStateTransitionException(
                "Invalid state transition from " + currentState + " to " + newState + 
                ". Allowed transitions: " + (allowedStates != null ? allowedStates : "none")
            );
        }
    }
}
