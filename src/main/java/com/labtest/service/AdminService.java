package com.labtest.service;

import com.labtest.dto.LabTestRequest;
import com.labtest.dto.LabTestResponse;
import com.labtest.dto.TimeSlotRequest;
import com.labtest.dto.TimeSlotResponse;
import com.labtest.entity.LabTest;
import com.labtest.entity.Laboratory;
import com.labtest.entity.TimeSlot;
import com.labtest.exception.ConflictException;
import com.labtest.exception.ResourceNotFoundException;
import com.labtest.exception.ValidationException;
import com.labtest.repository.LabTestRepository;
import com.labtest.repository.LaboratoryRepository;
import com.labtest.repository.TimeSlotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {
    
    @Autowired
    private TimeSlotRepository timeSlotRepository;
    
    @Autowired
    private LabTestRepository labTestRepository;
    
    @Autowired
    private LaboratoryRepository laboratoryRepository;
    
    @Transactional
    public TimeSlotResponse createTimeSlot(TimeSlotRequest request) {
        // Fetch laboratory
        Laboratory laboratory = laboratoryRepository.findById(request.getLaboratoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Laboratory not found"));
        
        if (!laboratory.getActive()) {
            throw new ValidationException("Cannot create time slot for inactive laboratory");
        }
        
        TimeSlot timeSlot = new TimeSlot();
        timeSlot.setLaboratory(laboratory);
        timeSlot.setSlotTime(request.getSlotTime());
        timeSlot.setTotalCapacity(request.getTotalCapacity());
        timeSlot.setRemainingCapacity(request.getTotalCapacity());
        
        TimeSlot saved = timeSlotRepository.save(timeSlot);
        
        return mapToResponse(saved);
    }
    
    @Transactional
    public TimeSlotResponse updateTimeSlotCapacity(Long timeSlotId, Integer newCapacity) {
        if (newCapacity < 1) {
            throw new ValidationException("Capacity must be at least 1");
        }
        
        TimeSlot timeSlot = timeSlotRepository.findById(timeSlotId)
                .orElseThrow(() -> new ResourceNotFoundException("Time slot not found"));
        
        // Calculate booked count from single source of truth
        int bookedCount = timeSlot.getTotalCapacity() - timeSlot.getRemainingCapacity();
        
        // Validate: new capacity cannot be less than already booked count
        if (newCapacity < bookedCount) {
            throw new ValidationException(
                "New capacity (" + newCapacity + ") cannot be less than already booked count (" 
                + bookedCount + ")"
            );
        }
        
        timeSlot.setTotalCapacity(newCapacity);
        timeSlot.setRemainingCapacity(newCapacity - bookedCount);
        
        TimeSlot updated = timeSlotRepository.save(timeSlot);
        
        return mapToResponse(updated);
    }
    
    private TimeSlotResponse mapToResponse(TimeSlot timeSlot) {
        // Calculate booked count from single source of truth
        int bookedCount = timeSlot.getTotalCapacity() - timeSlot.getRemainingCapacity();
        
        Laboratory lab = timeSlot.getLaboratory();
        
        return new TimeSlotResponse(
            timeSlot.getId(),
            lab.getId(),
            lab.getName(),
            lab.getCity(),
            timeSlot.getSlotTime(),
            timeSlot.getTotalCapacity(),
            timeSlot.getRemainingCapacity(),
            bookedCount
        );
    }
    
    @Transactional
    public LabTestResponse createLabTest(LabTestRequest request) {
        // Check if lab test with same name already exists
        if (labTestRepository.findByName(request.getName()).isPresent()) {
            throw new ConflictException("Lab test with name '" + request.getName() + "' already exists");
        }
        
        LabTest labTest = new LabTest();
        labTest.setName(request.getName());
        labTest.setDescription(request.getDescription());
        labTest.setAverageProcessingMinutes(request.getAverageProcessingMinutes());
        
        LabTest saved = labTestRepository.save(labTest);
        
        return new LabTestResponse(
            saved.getId(),
            saved.getName(),
            saved.getDescription(),
            saved.getAverageProcessingMinutes()
        );
    }
    
    public List<LabTestResponse> getAllLabTests() {
        return labTestRepository.findAll().stream()
            .map(labTest -> new LabTestResponse(
                labTest.getId(),
                labTest.getName(),
                labTest.getDescription(),
                labTest.getAverageProcessingMinutes()
            ))
            .collect(Collectors.toList());
    }
}
