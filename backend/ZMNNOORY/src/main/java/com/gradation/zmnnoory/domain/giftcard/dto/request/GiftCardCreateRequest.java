package com.gradation.zmnnoory.domain.giftcard.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "기프티콘 등록 요청")
public record GiftCardCreateRequest(

        @Schema(description = "상품 ID", example = "1")
        @NotNull(message = "상품 ID는 필수입니다.")
        Long productId,

        @Schema(description = "기프티콘 이미지 (Base64 인코딩)", example = "data:image/png;base64,...")
        @NotNull(message = "기프티콘 이미지는 필수입니다.")
        String giftCardImage
) {
}