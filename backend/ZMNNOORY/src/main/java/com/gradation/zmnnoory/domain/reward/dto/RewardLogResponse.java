package com.gradation.zmnnoory.domain.reward.dto;

import com.gradation.zmnnoory.domain.reward.entity.RewardLog;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class RewardLogResponse {

    private final UUID id;
    private final Long memberId;
    private final String memberEmail;
    private final Long stageId;
    private final String stageTitle;
    private final int point;
    private final LocalDate earnedAt;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public static RewardLogResponse from(RewardLog rewardLog) {
        return RewardLogResponse.builder()
                .id(rewardLog.getId())
                .memberId(rewardLog.getMember().getId())
                .memberEmail(rewardLog.getMember().getEmail())
                .stageId(rewardLog.getStage().getId())
                .stageTitle(rewardLog.getStage().getTitle())
                .point(rewardLog.getPoint())
                .earnedAt(rewardLog.getEarnedAt())
                .createdAt(rewardLog.getCreatedAt())
                .updatedAt(rewardLog.getUpdatedAt())
                .build();
    }
}