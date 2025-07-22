package com.gradation.zmnnoory.domain.reward.dto;

import com.gradation.zmnnoory.domain.reward.entity.RewardLog;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class RewardLogResponse {

    private final String memberEmail;
    private final String stageTitle;
    private final int point;
    private final LocalDateTime rewardedAt;

    @Builder
    private RewardLogResponse(String memberEmail, String stageTitle, int point, LocalDateTime rewardedAt) {
        this.memberEmail = memberEmail;
        this.stageTitle = stageTitle;
        this.point = point;
        this.rewardedAt = rewardedAt;
    }

    public static RewardLogResponse of(RewardLog rewardLog) {
        return RewardLogResponse.builder()
                .memberEmail(rewardLog.getMember().getEmail())
                .stageTitle(rewardLog.getStage().getTitle())
                .point(rewardLog.getPoint())
                .rewardedAt(rewardLog.getCreatedAt())
                .build();
    }
}