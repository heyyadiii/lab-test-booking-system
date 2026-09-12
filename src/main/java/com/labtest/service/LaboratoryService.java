package com.labtest.service;

import com.labtest.dto.LaboratoryRequest;
import com.labtest.dto.LaboratoryResponse;
import com.labtest.entity.Laboratory;
import com.labtest.exception.ResourceNotFoundException;
import com.labtest.repository.LaboratoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LaboratoryService {
    
    @Autowired
    private LaboratoryRepository laboratoryRepository;
    
    @Transactional
    public LaboratoryResponse createLaboratory(LaboratoryRequest request) {
        Laboratory laboratory = new Laboratory();
        laboratory.setName(request.getName());
        laboratory.setAddress(request.getAddress());
        laboratory.setCity(request.getCity());
        laboratory.setState(request.getState());
        laboratory.setPincode(request.getPincode());
        laboratory.setPhone(request.getPhone());
        laboratory.setEmail(request.getEmail());
        laboratory.setDescription(request.getDescription());
        laboratory.setActive(true);
        
        laboratory = laboratoryRepository.save(laboratory);
        return mapToResponse(laboratory);
    }
    
    public List<LaboratoryResponse> getAllLaboratories() {
        return laboratoryRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    public List<LaboratoryResponse> getActiveLaboratories() {
        return laboratoryRepository.findByActiveTrue().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    public List<LaboratoryResponse> getLaboratoriesByCity(String city) {
        return laboratoryRepository.findByCityAndActiveTrue(city).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    public List<LaboratoryResponse> getLaboratoriesByState(String state) {
        return laboratoryRepository.findByStateAndActiveTrue(state).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    public LaboratoryResponse getLaboratoryById(Long id) {
        Laboratory laboratory = laboratoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Laboratory not found"));
        return mapToResponse(laboratory);
    }
    
    @Transactional
    public LaboratoryResponse updateLaboratory(Long id, LaboratoryRequest request) {
        Laboratory laboratory = laboratoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Laboratory not found"));
        
        laboratory.setName(request.getName());
        laboratory.setAddress(request.getAddress());
        laboratory.setCity(request.getCity());
        laboratory.setState(request.getState());
        laboratory.setPincode(request.getPincode());
        laboratory.setPhone(request.getPhone());
        laboratory.setEmail(request.getEmail());
        laboratory.setDescription(request.getDescription());
        
        laboratory = laboratoryRepository.save(laboratory);
        return mapToResponse(laboratory);
    }
    
    @Transactional
    public void deactivateLaboratory(Long id) {
        Laboratory laboratory = laboratoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Laboratory not found"));
        laboratory.setActive(false);
        laboratoryRepository.save(laboratory);
    }
    
    @Transactional
    public void activateLaboratory(Long id) {
        Laboratory laboratory = laboratoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Laboratory not found"));
        laboratory.setActive(true);
        laboratoryRepository.save(laboratory);
    }
    
    private LaboratoryResponse mapToResponse(Laboratory laboratory) {
        return new LaboratoryResponse(
            laboratory.getId(),
            laboratory.getName(),
            laboratory.getAddress(),
            laboratory.getCity(),
            laboratory.getState(),
            laboratory.getPincode(),
            laboratory.getPhone(),
            laboratory.getEmail(),
            laboratory.getDescription(),
            laboratory.getActive(),
            laboratory.getCreatedAt()
        );
    }
}
