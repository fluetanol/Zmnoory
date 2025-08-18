package com.gradation.zmnnoory.domain.product.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Category {

    FOOD("음식"),
    GIFT("상품권"),
    CAFE("카페"),
    ;

    private final String desc;

}
