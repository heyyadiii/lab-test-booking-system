package com.labtest.controller;

import com.labtest.dto.CreateReportRequest;
import com.labtest.dto.ReportResponse;
import com.labtest.service.ReportService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
    
    @Autowired
    private ReportService reportService;
    
    /**
     * Create/Upload report (Technician only)
     * POST /api/reports
     */
    @PostMapping
    @PreAuthorize("hasRole('LAB_TECHNICIAN')")
    public ResponseEntity<ReportResponse> createReport(
            @Valid @RequestBody CreateReportRequest request,
            Authentication authentication) {
        String technicianUsername = authentication.getName();
        ReportResponse report = reportService.createReport(request, technicianUsername);
        return ResponseEntity.status(HttpStatus.CREATED).body(report);
    }
    
    /**
     * Get report by sample ID (Patient only - their own reports)
     * GET /api/reports/{sampleId}
     */
    @GetMapping("/{sampleId}")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<ReportResponse> getReportBySampleId(
            @PathVariable Long sampleId,
            Authentication authentication) {
        String username = authentication.getName();
        ReportResponse report = reportService.getReportBySampleId(sampleId, username);
        return ResponseEntity.ok(report);
    }
    
    /**
     * Download PDF report (Patient only - their own reports)
     * GET /api/reports/{sampleId}/pdf
     */
    @GetMapping("/{sampleId}/pdf")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<byte[]> downloadReportPDF(
            @PathVariable Long sampleId,
            Authentication authentication) {
        String username = authentication.getName();
        byte[] pdfBytes = reportService.generatePDF(sampleId, username);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "report-" + sampleId + ".pdf");
        headers.setContentLength(pdfBytes.length);
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }
}
