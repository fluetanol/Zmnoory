package com.gradation.zmnnoory.domain.order.dto.response;


import com.gradation.zmnnoory.domain.product.entity.Product;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
public class OrderResponse {

    @Schema(description = "상품명", example = "bhc 뿌링클")
    private String productTitle;

    @Schema(description = "상품 가격", example = "20,000")
    private Long productPrice;

    @Schema(description = "상품 썸네일", example = "http...")
    private String productThumbnail;

    @Builder
    private OrderResponse(String productTitle, Long productPrice, String productThumbnail) {
        this.productTitle = productTitle;
        this.productPrice = productPrice;
        this.productThumbnail = productThumbnail;
    }

    public static OrderResponse of(Product product) {
        return OrderResponse.builder()
                .productTitle(product.getTitle())
                .productPrice(product.getPrice())
                .productThumbnail(product.getThumbnail())
                .build();
    }


}
