package com.labtest.service;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.labtest.dto.CreateReportRequest;
import com.labtest.dto.ReportResponse;
import com.labtest.entity.Report;
import com.labtest.entity.Sample;
import com.labtest.entity.SampleState;
import com.labtest.entity.User;
import com.labtest.exception.ForbiddenException;
import com.labtest.exception.ResourceNotFoundException;
import com.labtest.exception.ValidationException;
import com.labtest.repository.ReportRepository;
import com.labtest.repository.SampleRepository;
import com.labtest.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
public class ReportService {
    
    @Autowired
    private ReportRepository reportRepository;
    
    @Autowired
    private SampleRepository sampleRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    private static final DateTimeFormatter DATE_FORMATTER = 
        DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");
    
    @Transactional
    public ReportResponse createReport(CreateReportRequest request, String technicianUsername) {
        // Verify technician role
        User technician = userRepository.findByUsername(technicianUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        if (!technician.getRole().name().equals("LAB_TECHNICIAN")) {
            throw new ForbiddenException("Only lab technicians can create reports");
        }
        
        // Fetch sample
        Sample sample = sampleRepository.findById(request.getSampleId())
                .orElseThrow(() -> new ResourceNotFoundException("Sample not found"));
        
        // Verify sample is in COMPLETED state
        if (sample.getState() != SampleState.COMPLETED) {
            throw new ValidationException(
                "Report can only be created for COMPLETED samples. Current state: " + sample.getState()
            );
        }
        
        // Check if report already exists
        if (reportRepository.findBySample(sample).isPresent()) {
            throw new ValidationException("Report already exists for this sample");
        }
        
        // Create report
        Report report = new Report();
        report.setSample(sample);
        report.setResults(request.getResults());
        report.setFindings(request.getFindings());
        report.setRecommendations(request.getRecommendations());
        
        report = reportRepository.save(report);
        
        // Update sample state to REPORTED
        sample.setState(SampleState.REPORTED);
        sampleRepository.save(sample);
        
        return mapToResponse(report);
    }
    
    public ReportResponse getReportBySampleId(Long sampleId, String username) {
        // Fetch sample
        Sample sample = sampleRepository.findById(sampleId)
                .orElseThrow(() -> new ResourceNotFoundException("Sample not found"));
        
        // Verify sample is in REPORTED state
        if (sample.getState() != SampleState.REPORTED) {
            throw new ValidationException(
                "Report is not yet available. Current sample state: " + sample.getState()
            );
        }
        
        // Verify ownership - user can only access their own reports
        if (!sample.getBooking().getUser().getUsername().equals(username)) {
            throw new ForbiddenException(
                "You are not authorized to access this report"
            );
        }
        
        // Fetch report
        Report report = reportRepository.findBySample(sample)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found"));
        
        return mapToResponse(report);
    }
    
    public byte[] generatePDF(Long sampleId, String username) {
        // Fetch sample
        Sample sample = sampleRepository.findById(sampleId)
                .orElseThrow(() -> new ResourceNotFoundException("Sample not found"));
        
        // Verify ownership
        if (!sample.getBooking().getUser().getUsername().equals(username)) {
            throw new ForbiddenException(
                "You are not authorized to access this report"
            );
        }
        
        // Fetch report
        Report report = reportRepository.findBySample(sample)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found"));
        
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);
            
            // Title
            Paragraph title = new Paragraph("LAB TEST REPORT")
                    .setFontSize(24)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(ColorConstants.BLUE);
            document.add(title);
            
            // Lab name
            Paragraph labName = new Paragraph("LabFlow Medical Laboratory")
                    .setFontSize(14)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20);
            document.add(labName);
            
            // Patient Information Table
            Table infoTable = new Table(UnitValue.createPercentArray(new float[]{1, 2}))
                    .useAllAvailableWidth()
                    .setMarginBottom(20);
            
            infoTable.addCell("Sample ID:");
            infoTable.addCell("#" + sample.getId());
            
            infoTable.addCell("Patient Name:");
            infoTable.addCell(sample.getBooking().getUser().getFullName());
            
            infoTable.addCell("Lab Test:");
            infoTable.addCell(sample.getBooking().getLabTest().getName());
            
            infoTable.addCell("Report Date:");
            infoTable.addCell(report.getGeneratedAt().format(DATE_FORMATTER));
            
            document.add(infoTable);
            
            // Test Results Section
            document.add(new Paragraph("TEST RESULTS")
                    .setFontSize(16)
                    .setBold()
                    .setMarginTop(20)
                    .setFontColor(ColorConstants.BLUE));
            
            document.add(new Paragraph(report.getResults())
                    .setMarginBottom(15));
            
            // Findings Section
            document.add(new Paragraph("FINDINGS")
                    .setFontSize(16)
                    .setBold()
                    .setMarginTop(10)
                    .setFontColor(ColorConstants.BLUE));
            
            document.add(new Paragraph(report.getFindings())
                    .setMarginBottom(15));
            
            // Recommendations Section (if present)
            if (report.getRecommendations() != null && !report.getRecommendations().isEmpty()) {
                document.add(new Paragraph("RECOMMENDATIONS")
                        .setFontSize(16)
                        .setBold()
                        .setMarginTop(10)
                        .setFontColor(ColorConstants.BLUE));
                
                document.add(new Paragraph(report.getRecommendations())
                        .setMarginBottom(15));
            }
            
            // Footer
            document.add(new Paragraph("\n\n---")
                    .setTextAlignment(TextAlignment.CENTER));
            
            document.add(new Paragraph("This is a computer-generated report.")
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setItalic());
            
            document.close();
            
            return baos.toByteArray();
            
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF: " + e.getMessage(), e);
        }
    }
    
    private ReportResponse mapToResponse(Report report) {
        Sample sample = report.getSample();
        return new ReportResponse(
            report.getId(),
            sample.getId(),
            sample.getBooking().getUser().getFullName(),
            sample.getBooking().getLabTest().getName(),
            report.getResults(),
            report.getFindings(),
            report.getRecommendations(),
            report.getGeneratedAt()
        );
    }
}
