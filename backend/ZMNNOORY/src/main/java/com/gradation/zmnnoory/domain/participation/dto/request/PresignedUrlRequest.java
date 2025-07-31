package com.gradation.zmnnoory.domain.participation.dto.request;

import jakarta.validation.constraints.NotBlank;

public record PresignedUrlRequest(
        @NotBlank(message = "이메일은 필수입니다.")
        String email,

        @NotBlank(message = "게임 제목은 필수입니다.")
        String gameTitle,

        @NotBlank(message = "파일명은 필수입니다.")
        String fileName,

        @NotBlank(message = "컨텐츠 타입은 필수입니다.")
        String contentType
) {
}