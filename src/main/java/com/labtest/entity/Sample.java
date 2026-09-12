package com.labtest.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "samples")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Sample {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SampleState state;
    
    @JsonIgnore
    @OneToOne(mappedBy = "sample", cascade = CascadeType.ALL)
    private Report report;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime collectedAt;
    
    private LocalDateTime testStartedAt;
    
    private LocalDateTime completedAt;
    
    private LocalDateTime reportedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
