package com.gradation.zmnnoory.domain.participation.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class StartParticipationRequest {
    
    @NotNull(message = "회원 ID는 필수입니다")
    @Positive(message = "회원 ID는 양수여야 합니다")
    private Long memberId;
    
    @NotNull(message = "스테이지 ID는 필수입니다")
    @Positive(message = "스테이지 ID는 양수여야 합니다")
    private Long stageId;
}