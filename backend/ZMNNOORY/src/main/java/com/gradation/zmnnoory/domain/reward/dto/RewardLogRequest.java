package com.gradation.zmnnoory.domain.reward.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "리워드 지급 요청 DTO")
public record RewardLogRequest(

        @Schema(description = "참여 ID", example = "d290f1ee-6c54-4b01-90e6-d701748f0851")
        @NotNull(message = "참여 ID는 필수 값입니다.")
        Long participationId
) {
}
