package com.gradation.zmnnoory.domain.giftcard.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GiftCardStatus {

    AVAILABLE("사용 가능"),
    ASSIGNED("할당됨"),
    USED("사용 완료"),
    EXPIRED("만료됨"),
    ;

    private final String desc;
}