package com.labtest.repository;

import com.labtest.entity.Sample;
import com.labtest.entity.SampleState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SampleRepository extends JpaRepository<Sample, Long> {
    
    List<Sample> findByStateOrderByCreatedAtAsc(SampleState state);
    
    List<Sample> findByStateAndCreatedAtBefore(SampleState state, LocalDateTime timestamp);
}
