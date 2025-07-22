package com.gradation.zmnnoory.domain.participation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateParticipationRequest {
    private final Integer frameCount;
    private final String videoUrl;
    private final String thumbnailUrl;
}