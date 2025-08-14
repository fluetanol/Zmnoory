package com.gradation.zmnnoory.domain.giftcard.dto.response;

import com.gradation.zmnnoory.domain.giftcard.entity.GiftCard;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MyGiftCardResponse {

    @Schema(description = "기프티콘 ID", example = "1")
    private Long id;

    @Schema(description = "기프티콘 이미지 (Base64 인코딩)", example = "data:image/png;base64,...")
    private String giftCardImage;

    @Schema(description = "상품명", example = "스타벅스 아메리카노")
    private String productTitle;

    @Schema(description = "상품 썸네일", example = "https://example.com/image.jpg")
    private String productThumbnail;

    @Schema(description = "할당일", example = "2024-01-01T00:00:00")
    private LocalDateTime assignedAt; // Renamed from purchasedAt to assignedAt

    @Builder
    private MyGiftCardResponse(Long id,
                               String giftCardImage,
                               String productTitle,
                               String productThumbnail,
                               LocalDateTime assignedAt) {
        this.id = id;
        this.giftCardImage = giftCardImage;
        this.productTitle = productTitle;
        this.productThumbnail = productThumbnail;
        this.assignedAt = assignedAt;
    }

    public static MyGiftCardResponse from(GiftCard giftCard) {
        return MyGiftCardResponse.builder()
                .id(giftCard.getId())
                .giftCardImage(giftCard.getGiftCardImage())
                .productTitle(giftCard.getProduct().getTitle())
                .productThumbnail(giftCard.getProduct().getThumbnail())
                .assignedAt(giftCard.getCreatedAt()) // Assuming createdAt is when it's assigned to a member
                .build();
    }
}
