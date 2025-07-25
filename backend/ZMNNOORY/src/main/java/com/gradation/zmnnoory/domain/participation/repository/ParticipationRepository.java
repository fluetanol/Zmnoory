package com.gradation.zmnnoory.domain.participation.repository;

import com.gradation.zmnnoory.domain.participation.entity.Participation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParticipationRepository extends JpaRepository<Participation, Long> {

    List<Participation> findByMemberId(Long memberId);
    
    Optional<Participation> findByMemberEmailAndGameTitle(String email, String gameTitle);
    
    boolean existsByMemberEmailAndGameTitle(String email, String gameTitle);
}