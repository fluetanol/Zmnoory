package com.gradation.zmnnoory.domain.participation.dto;

import lombok.Data;

@Data
public class UpdateParticipationRequest {
    private Integer frameCount;
    private String videoUrl;
    private String thumbnailUrl;
}