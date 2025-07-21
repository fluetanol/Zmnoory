package com.gradation.zmnnoory.domain.participation.repository;

import com.gradation.zmnnoory.domain.participation.entity.Participation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ParticipationRepository extends JpaRepository<Participation, UUID> {
    
    boolean existsByMemberIdAndStageId(Long memberId, Long stageId);
    
}