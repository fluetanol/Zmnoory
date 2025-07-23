package com.gradation.zmnnoory.domain.stage.repository;

import com.gradation.zmnnoory.domain.stage.entity.Stage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StageRepository extends JpaRepository<Stage, Long> {
    
    Optional<Stage> findByTitle(String title);
}
