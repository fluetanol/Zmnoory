package com.gradation.zmnnoory.domain.reward.service;

import com.gradation.zmnnoory.domain.participation.entity.Participation;
import com.gradation.zmnnoory.domain.participation.repository.ParticipationRepository;
import com.gradation.zmnnoory.domain.reward.entity.RewardLog;
import com.gradation.zmnnoory.domain.reward.repository.RewardLogRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RewardLogService {

    private final RewardLogRepository rewardLogRepository;
    private final ParticipationRepository participationRepository;

    @Transactional
    public RewardLog giveReward(UUID participationId) {
        Participation participation = participationRepository.findById(participationId)
                .orElseThrow(() -> new EntityNotFoundException("참여 정보를 찾을 수 없습니다."));

        Long memberId = participation.getMember().getId();
        Long stageId = participation.getStage().getId();

        boolean alreadyGiven = rewardLogRepository.existsByMemberIdAndStageId(memberId, stageId);
        if (alreadyGiven) {
            throw new IllegalStateException("이미 해당 스테이지에 대한 보상을 지급받았습니다.");
        }

        // Stage에서 리워드 포인트 가져오기
        int rewardPoint = participation.getStage().getRewardTotal();

        RewardLog rewardLog = RewardLog.builder()
                .member(participation.getMember())
                .stage(participation.getStage())
                .point(rewardPoint)
                .build();

        return rewardLogRepository.save(rewardLog);
    }

    public List<RewardLog> getRewardLogsByMember(Long memberId) {
        return rewardLogRepository.findByMemberId(memberId);
    }
    
    public RewardLog findById(UUID id) {
        return rewardLogRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("리워드 로그를 찾을 수 없습니다."));
    }
    
    public List<RewardLog> findAll() {
        return rewardLogRepository.findAll();
    }
    
    public boolean hasReceivedReward(Long memberId, Long stageId) {
        return rewardLogRepository.existsByMemberIdAndStageId(memberId, stageId);
    }
}
