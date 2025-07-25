package com.gradation.zmnnoory.domain.reward.dto;

import com.gradation.zmnnoory.domain.reward.entity.RewardLog;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class RewardLogResponse {

    private final String memberEmail;
    private final String gameTitle;
    private final int point;
    private final LocalDateTime rewardedAt;

    @Builder
    private RewardLogResponse(String memberEmail, String gameTitle, int point, LocalDateTime rewardedAt) {
        this.memberEmail = memberEmail;
        this.gameTitle = gameTitle;
        this.point = point;
        this.rewardedAt = rewardedAt;
    }

    public static RewardLogResponse of(RewardLog rewardLog) {
        return RewardLogResponse.builder()
                .memberEmail(rewardLog.getMember().getEmail())
                .gameTitle(rewardLog.getGame().getTitle())
                .point(rewardLog.getPoint())
                .rewardedAt(rewardLog.getCreatedAt())
                .build();
    }
}