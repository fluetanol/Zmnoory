package com.gradation.zmnnoory.domain.product.entity;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Category {

    FOOD("음식"),
    GIFT("상품권"),
    CAFE("카페"),
    ;

    private final String desc;

}
