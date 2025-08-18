package com.gradation.zmnnoory.domain.giftcard.dto.response;

import com.gradation.zmnnoory.domain.giftcard.entity.GiftCard;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class GiftCardResponse {

    @Schema(description = "기프티콘 ID", example = "1")
    private Long id;

    @Schema(description = "기프티콘 이미지 (Base64 인코딩)", example = "data:image/png;base64,...")
    private String giftCardImage;

    @Schema(description = "상품명", example = "스타벅스 아메리카노")
    private String productTitle;

    @Schema(description = "상품 가격", example = "5000")
    private Long productPrice;

    @Schema(description = "상품 썸네일", example = "https://example.com/image.jpg")
    private String productThumbnail;

    @Schema(description = "생성일", example = "2024-01-01T00:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "구매 여부", example = "true")
    private Boolean isPurchased;

    @Schema(description = "구매자 닉네임", example = "홍길동")
    private String purchasedBy;

    @Schema(description = "구매일", example = "2024-01-15T10:30:00")
    private LocalDateTime purchasedAt;

    @Builder
    private GiftCardResponse(Long id,
                             String giftCardImage,
                             String productTitle,
                             Long productPrice,
                             String productThumbnail,
                             LocalDateTime createdAt,
                             Boolean isPurchased,
                             String purchasedBy,
                             LocalDateTime purchasedAt) {
        this.id = id;
        this.giftCardImage = giftCardImage;
        this.productTitle = productTitle;
        this.productPrice = productPrice;
        this.productThumbnail = productThumbnail;
        this.createdAt = createdAt;
        this.isPurchased = isPurchased;
        this.purchasedBy = purchasedBy;
        this.purchasedAt = purchasedAt;
    }

    public static GiftCardResponse from(GiftCard giftCard) {
        boolean isPurchased = giftCard.getMember() != null;
        return GiftCardResponse.builder()
                .id(giftCard.getId())
                .giftCardImage(giftCard.getGiftCardImage())
                .productTitle(giftCard.getProduct().getTitle())
                .productPrice(giftCard.getProduct().getPrice())
                .productThumbnail(giftCard.getProduct().getThumbnail())
                .createdAt(giftCard.getCreatedAt())
                .isPurchased(isPurchased)
                .purchasedBy(isPurchased ? giftCard.getMember().getNickname() : null)
                .purchasedAt(isPurchased ? giftCard.getUpdatedAt() : null)
                .build();
    }
}