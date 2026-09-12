package com.labtest.repository;

import com.labtest.entity.Report;
import com.labtest.entity.Sample;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    
    Optional<Report> findBySample(Sample sample);
}
