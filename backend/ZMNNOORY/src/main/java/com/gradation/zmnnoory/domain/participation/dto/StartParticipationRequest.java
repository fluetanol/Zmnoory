package com.gradation.zmnnoory.domain.participation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "참여 시작 요청 DTO")
public record StartParticipationRequest(
        @Schema(description = "회원 이메일", example = "user@example.com")
        @NotBlank(message = "회원 이메일은 필수입니다")
        @Email(message = "이메일 형식이 아닙니다")
        String email,

        @Schema(description = "스테이지 제목", example = "첫 번째 스테이지")
        @NotBlank(message = "스테이지 제목은 필수입니다")
        String stageTitle
) {
}
