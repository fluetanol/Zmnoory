package com.gradation.zmnnoory.domain.product.dto.response;

import com.gradation.zmnnoory.domain.product.entity.Product;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ProductResponse {

    @Schema(description = "상품 ID", example = "1")
    private final Long id;

    @Schema(description = "상품 제목", example = "스타벅스 아메리카노")
    private final String title;

    @Schema(description = "상품 카테고리", example = "카페")
    private final String category;

    @Schema(description = "상품 가격", example = "4500")
    private final Long price;

    @Schema(description = "상품 썸네일 이미지 URL", example = "https://example.com/image.png")
    private final String thumbnail;

    @Builder
    private ProductResponse(Long id, String title, String category, Long price, String thumbnail) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.price = price;
        this.thumbnail = thumbnail;
    }

    public static ProductResponse of(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .title(product.getTitle())
                .category(product.getCategory().getDesc())
                .price(product.getPrice())
                .thumbnail(product.getThumbnail())
                .build();
    }
}