package com.gradation.zmnnoory.domain.reward.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class RewardLogRequest {
    
    @NotNull(message = "참여 ID는 필수 값입니다.")
    private final UUID participationId;
}
