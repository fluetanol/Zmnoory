package com.gradation.zmnnoory.domain.product.dto.request;

import com.gradation.zmnnoory.domain.product.entity.Category;
import com.gradation.zmnnoory.domain.product.entity.Product;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Schema(description = "상품 생성 요청 DTO")
public record ProductCreateRequest(
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

        @Schema(description = "상품 썸네일 이미지", example = "image.png")
        @NotBlank(message = "썸네일은 필수입니다")
        String thumbnail
) {
    public Product toEntity() {
        return Product.builder()
                .title(this.title)
                .category(this.category)
                .price(this.price)
                .thumbnail(this.thumbnail)
                .build();
    }
}