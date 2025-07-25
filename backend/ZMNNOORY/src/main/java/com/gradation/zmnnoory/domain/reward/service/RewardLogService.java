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
        Long gameId = participation.getGame().getId();

        boolean alreadyGiven = rewardLogRepository.existsByMemberIdAndGameId(memberId, gameId);
        if (alreadyGiven) {
            throw new IllegalStateException("이미 해당 게임에 대한 보상을 지급받았습니다.");
        }

        // Game에서 리워드 포인트 가져오기
        int rewardPoint = participation.getGame().getPoint().intValue();

        RewardLog rewardLog = RewardLog.builder()
                .member(participation.getMember())
                .game(participation.getGame())
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
    
    public boolean hasReceivedReward(Long memberId, Long gameId) {
        return rewardLogRepository.existsByMemberIdAndGameId(memberId, gameId);
    }
}
