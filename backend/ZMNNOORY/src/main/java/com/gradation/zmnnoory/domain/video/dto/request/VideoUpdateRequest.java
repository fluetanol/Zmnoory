package com.gradation.zmnnoory.domain.video.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record VideoUpdateRequest(
        @Size(max = 30, message = "제목은 30자를 초과할 수 없습니다.")
        String title,

        @Size(max = 255, message = "설명은 255자를 초과할 수 없습니다.")
        String description,

        Boolean isPublic,

        @NotBlank(message = "비디오 URL은 필수입니다.")
        @Size(max = 255, message = "비디오 URL은 255자를 초과할 수 없습니다.")
        String videoUrl,

        @Size(max = 255, message = "썸네일 URL은 255자를 초과할 수 없습니다.")
        String thumbnailUrl
) {
}