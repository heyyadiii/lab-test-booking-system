package com.labtest.repository;

import com.labtest.entity.Laboratory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LaboratoryRepository extends JpaRepository<Laboratory, Long> {
    
    List<Laboratory> findByActiveTrue();
    
    List<Laboratory> findByCityAndActiveTrue(String city);
    
    List<Laboratory> findByStateAndActiveTrue(String state);
}
