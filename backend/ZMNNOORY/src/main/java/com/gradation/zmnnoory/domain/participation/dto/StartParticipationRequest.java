package com.gradation.zmnnoory.domain.participation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StartParticipationRequest {
    
    @NotNull(message = "회원 ID는 필수 값입니다.")
    private final Long memberId;
    
    @NotNull(message = "스테이지 ID는 필수 값입니다.")
    private final Long stageId;
}