package com.labtest.repository;

import com.labtest.entity.LabTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LabTestRepository extends JpaRepository<LabTest, Long> {
    
    Optional<LabTest> findByName(String name);
}
