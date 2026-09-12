package com.labtest.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "reports")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Report {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sample_id", nullable = false)
    private Sample sample;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String results;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String findings;
    
    @Column(columnDefinition = "TEXT")
    private String recommendations;
    
    private LocalDateTime generatedAt;
    
    @PrePersist
    protected void onCreate() {
        generatedAt = LocalDateTime.now();
    }
}
