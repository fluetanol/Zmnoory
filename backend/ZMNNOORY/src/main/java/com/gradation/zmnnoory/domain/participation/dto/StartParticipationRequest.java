package com.gradation.zmnnoory.domain.participation.dto;

import lombok.Data;

@Data
public class StartParticipationRequest {
    private Long memberId;
    private Long stageId;
}