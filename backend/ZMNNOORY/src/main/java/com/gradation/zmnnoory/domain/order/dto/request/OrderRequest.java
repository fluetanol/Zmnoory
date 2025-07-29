package com.gradation.zmnnoory.domain.order.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "구매 요청")
public record OrderRequest(

        @Schema(description = "상품명", example = "bhc 뿌링클")
        @NotNull(message = "상품명은 필수입니다.")
        String productTitle
) {
}