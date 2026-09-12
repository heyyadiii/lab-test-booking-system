package com.labtest.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "time_slots")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TimeSlot {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "laboratory_id", nullable = false)
    private Laboratory laboratory;
    
    @Column(nullable = false)
    private LocalDateTime slotTime;
    
    @Column(nullable = false)
    private Integer totalCapacity;
    
    @Column(nullable = false)
    private Integer remainingCapacity;
    
    @Version
    private Long version;
    
    @JsonIgnore
    @OneToMany(mappedBy = "timeSlot", cascade = CascadeType.ALL)
    private List<Booking> bookings;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
