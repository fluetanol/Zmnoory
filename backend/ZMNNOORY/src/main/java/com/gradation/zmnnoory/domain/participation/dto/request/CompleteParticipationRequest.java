package com.gradation.zmnnoory.domain.participation.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CompleteParticipationRequest(
        @NotBlank(message = "이메일은 필수입니다.")
        String email,

        @NotBlank(message = "게임 제목은 필수입니다.")
        String gameTitle,

        @NotBlank(message = "비디오 URL은 필수입니다.")
        String videoUrl,

        String thumbnailUrl,
        
        String title,
        
        String description
) {
}