package com.gradation.zmnnoory.domain.reward.repository;

import com.gradation.zmnnoory.domain.game.entity.Game;
import com.gradation.zmnnoory.domain.member.entity.Member;
import com.gradation.zmnnoory.domain.reward.entity.RewardLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RewardLogRepository extends JpaRepository<RewardLog, Long> {

    boolean existsByMemberIdAndGameId(Long memberId, Long gameId);

    Optional<RewardLog> findByMemberIdAndGameId(Long memberId, Long gameId);
    
    List<RewardLog> findByMemberId(Long memberId);

    boolean existsByMemberAndGame(Member member, Game game);

    Optional<RewardLog> findByMemberAndGame(Member member, Game game);
}
