package com.gradation.zmnnoory.domain.participation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "참여 업데이트 요청 DTO")
public record UpdateParticipationRequest(

        @Schema(description = "프레임 수 (0 이상, 최대 999999)", example = "120")
        @NotNull(message = "프레임 수는 필수입니다")
        @PositiveOrZero(message = "프레임 수는 0 이상이어야 합니다")
        @Max(value = 999999, message = "프레임 수는 999999 이하여야 합니다")
        Integer frameCount,

        @Schema(description = "비디오 URL", example = "https://example.com/video.mp4")
        @Size(max = 500, message = "비디오 URL은 500자 이하여야 합니다")
        @Pattern(
                regexp = "^(https?://).+",
                message = "올바른 URL 형식이어야 합니다"
        )
        String videoUrl,

        @Schema(description = "썸네일 URL", example = "https://example.com/thumbnail.jpg")
        @Size(max = 500, message = "썸네일 URL은 500자 이하여야 합니다")
        @Pattern(
                regexp = "^(https?://).+",
                message = "올바른 URL 형식이어야 합니다"
        )
        String thumbnailUrl

) {
}
