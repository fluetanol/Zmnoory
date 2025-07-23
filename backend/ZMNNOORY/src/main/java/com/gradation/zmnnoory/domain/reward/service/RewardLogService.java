package com.gradation.zmnnoory.domain.reward.service;

import com.gradation.zmnnoory.domain.participation.entity.Participation;
import com.gradation.zmnnoory.domain.participation.repository.ParticipationRepository;
import com.gradation.zmnnoory.domain.reward.dto.RewardLogResponse;
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
    public RewardLogResponse giveReward(UUID participationId) {
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

        RewardLog savedRewardLog = rewardLogRepository.save(rewardLog);
        return RewardLogResponse.of(savedRewardLog);
    }

    public List<RewardLogResponse> getRewardLogsByMember(Long memberId) {
        List<RewardLog> rewardLogs = rewardLogRepository.findByMemberId(memberId);
        return rewardLogs.stream()
                .map(RewardLogResponse::of)
                .toList();
    }
    
    public RewardLogResponse findById(UUID id) {
        RewardLog rewardLog = rewardLogRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("리워드 로그를 찾을 수 없습니다."));
        return RewardLogResponse.of(rewardLog);
    }
    
    public List<RewardLogResponse> findAll() {
        List<RewardLog> rewardLogs = rewardLogRepository.findAll();
        return rewardLogs.stream()
                .map(RewardLogResponse::of)
                .toList();
    }
    
    public boolean hasReceivedReward(Long memberId, Long stageId) {
        return rewardLogRepository.existsByMemberIdAndStageId(memberId, stageId);
    }
}
