package com.labtest.controller;

import com.labtest.dto.LaboratoryRequest;
import com.labtest.dto.LaboratoryResponse;
import com.labtest.service.LaboratoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/laboratories")
@Tag(name = "Laboratories", description = "Multi-laboratory management endpoints for location-based services")
public class LaboratoryController {
    
    @Autowired
    private LaboratoryService laboratoryService;
    
    @Operation(
        summary = "Create new laboratory",
        description = "Admin only: Create a new laboratory location with address, contact details, and description",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LaboratoryResponse> createLaboratory(
            @Valid @RequestBody LaboratoryRequest request) {
        LaboratoryResponse response = laboratoryService.createLaboratory(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @Operation(
        summary = "Get all active laboratories",
        description = "Public endpoint: Retrieve list of all active laboratory locations for patient selection"
    )
    @GetMapping
    public ResponseEntity<List<LaboratoryResponse>> getActiveLaboratories() {
        List<LaboratoryResponse> laboratories = laboratoryService.getActiveLaboratories();
        return ResponseEntity.ok(laboratories);
    }
    
    @Operation(
        summary = "Get all laboratories (including inactive)",
        description = "Admin only: Retrieve complete list of laboratories including deactivated ones",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<LaboratoryResponse>> getAllLaboratories() {
        List<LaboratoryResponse> laboratories = laboratoryService.getAllLaboratories();
        return ResponseEntity.ok(laboratories);
    }
    
    @Operation(
        summary = "Get laboratories by city",
        description = "Filter laboratories by city name for location-based search"
    )
    @GetMapping("/city/{city}")
    public ResponseEntity<List<LaboratoryResponse>> getLaboratoriesByCity(
            @Parameter(description = "City name", example = "Delhi")
            @PathVariable String city) {
        List<LaboratoryResponse> laboratories = laboratoryService.getLaboratoriesByCity(city);
        return ResponseEntity.ok(laboratories);
    }
    
    @Operation(
        summary = "Get laboratories by state",
        description = "Filter laboratories by state name for regional search"
    )
    @GetMapping("/state/{state}")
    public ResponseEntity<List<LaboratoryResponse>> getLaboratoriesByState(
            @Parameter(description = "State name", example = "Delhi")
            @PathVariable String state) {
        List<LaboratoryResponse> laboratories = laboratoryService.getLaboratoriesByState(state);
        return ResponseEntity.ok(laboratories);
    }
    
    @Operation(
        summary = "Get laboratory by ID",
        description = "Retrieve detailed information about a specific laboratory"
    )
    @GetMapping("/{id}")
    public ResponseEntity<LaboratoryResponse> getLaboratoryById(
            @Parameter(description = "Laboratory ID", example = "1")
            @PathVariable Long id) {
        LaboratoryResponse laboratory = laboratoryService.getLaboratoryById(id);
        return ResponseEntity.ok(laboratory);
    }
    
    @Operation(
        summary = "Update laboratory",
        description = "Admin only: Update laboratory information",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LaboratoryResponse> updateLaboratory(
            @Parameter(description = "Laboratory ID", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody LaboratoryRequest request) {
        LaboratoryResponse response = laboratoryService.updateLaboratory(id, request);
        return ResponseEntity.ok(response);
    }
    
    @Operation(
        summary = "Deactivate laboratory",
        description = "Admin only: Soft delete laboratory (sets active=false)",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deactivateLaboratory(
            @Parameter(description = "Laboratory ID", example = "1")
            @PathVariable Long id) {
        laboratoryService.deactivateLaboratory(id);
        return ResponseEntity.noContent().build();
    }
    
    @Operation(
        summary = "Activate laboratory",
        description = "Admin only: Reactivate a deactivated laboratory",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @PutMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> activateLaboratory(
            @Parameter(description = "Laboratory ID", example = "1")
            @PathVariable Long id) {
        laboratoryService.activateLaboratory(id);
        return ResponseEntity.ok().build();
    }
}
