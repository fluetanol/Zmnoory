package com.gradation.zmnnoory.domain.participation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateParticipationRequest {
    private Integer frameCount;
    private String videoUrl;
    private String thumbnailUrl;
}