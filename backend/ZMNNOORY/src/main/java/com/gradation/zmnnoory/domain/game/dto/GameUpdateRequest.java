package com.gradation.zmnnoory.domain.game.dto;

import com.gradation.zmnnoory.domain.game.entity.GameDifficulty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

@Schema(description = "게임 수정 요청 DTO")
public record GameUpdateRequest(

        @Schema(description = "게임 제목", example = "도형 맞추기")
        @NotBlank(message = "제목은 필수입니다")
        @Size(max = 30, message = "제목은 30자 이하여야 합니다")
        String title,

        @Schema(description = "간단 설명", example = "기하학 도형을 맞추는 게임입니다")
        @NotBlank(message = "설명은 필수입니다")
        @Size(max = 255, message = "설명은 255자 이하여야 합니다")
        String description,

        @Schema(description = "상세 설명", example = "회전 가능한 도형을 정해진 위치에 맞추는 게임입니다")
        @NotBlank(message = "상세 설명은 필수입니다")
        @Size(max = 255, message = "상세 설명은 255자 이하여야 합니다")
        String explanation,

        @Schema(description = "게임 난이도", example = "HARD")
        @NotNull(message = "난이도는 필수입니다")
        GameDifficulty difficulty,

        @Schema(description = "보상 포인트", example = "500")
        @NotNull(message = "포인트는 필수입니다")
        @Positive(message = "포인트는 양수여야 합니다")
        @Max(value = 10000, message = "포인트는 10000 이하여야 합니다")
        Long point,

        @Schema(description = "썸네일 이미지 URL", example = "https://example.com/image.png")
        @Size(max = 255, message = "썸네일 URL은 255자 이하여야 합니다")
        @Pattern(
                regexp = "^(https?://).+",
                message = "올바른 URL 형식이어야 합니다"
        )
        String thumbnail,

        @Schema(description = "요구 데이터 유형", example = "얼굴 데이터")
        String requiredDataType

) {}
