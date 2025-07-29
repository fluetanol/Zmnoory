package com.gradation.zmnnoory.domain.product.dto.request;

import com.gradation.zmnnoory.domain.product.entity.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "상품 수정 요청 DTO")
public record ProductUpdateRequest(
        @Schema(description = "상품 제목", example = "스타벅스 아메리카노")
        @NotBlank(message = "제목은 필수입니다")
        @Size(max = 50, message = "제목은 50자 이하여야 합니다")
        String title,

        @Schema(description = "상품 카테고리", example = "CAFE")
        @NotNull(message = "카테고리는 필수입니다")
        Category category,

        @Schema(description = "상품 가격", example = "4500")
        @NotNull(message = "가격은 필수입니다")
        @Positive(message = "가격은 양수여야 합니다")
        Long price,

        @Schema(description = "상품 썸네일 이미지 URL", example = "https://example.com/image.png")
        @NotBlank(message = "썸네일은 필수입니다")
        @Size(max = 255, message = "썸네일 URL은 255자 이하여야 합니다")
        @Pattern(
                regexp = "^(https?://).+",
                message = "올바른 URL 형식이어야 합니다"
        )
        String thumbnail
) {
}