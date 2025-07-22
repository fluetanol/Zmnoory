package com.gradation.zmnnoory.domain.reward.repository;

import com.gradation.zmnnoory.domain.member.entity.Member;
import com.gradation.zmnnoory.domain.reward.entity.RewardLog;
import com.gradation.zmnnoory.domain.stage.entity.Stage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RewardLogRepository extends JpaRepository<RewardLog, UUID> {

    boolean existsByMemberIdAndStageId(Long memberId, Long stageId);

    Optional<RewardLog> findByMemberIdAndStageId(Long memberId, Long stageId);
    
    List<RewardLog> findByMemberId(Long memberId);

    boolean existsByMemberAndStage(Member member, Stage stage);

    Optional<RewardLog> findByMemberAndStage(Member member, Stage stage);
}
