package com.gradation.zmnnoory.domain.order.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum OrderExceptionConstant {

    PRODUCT_NOT_FOUND_EXCEPTION("상품을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

    INSUFFICIENT_POINT_EXCEPTION("보유 포인트가 부족합니다.", HttpStatus.BAD_REQUEST);

    private final String message;

    private final HttpStatus status;
}
