package com.gradation.zmnnoory.domain.giftcard.exception;

import com.gradation.zmnnoory.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class GiftCardNotFoundException extends BaseException {

    public GiftCardNotFoundException() {
        super(GiftCardExceptionConstant.GIFT_CARD_NOT_FOUND.getMessage());
    }

    @Override
    public HttpStatus getStatus() {
        return GiftCardExceptionConstant.GIFT_CARD_NOT_FOUND.getHttpStatus();
    }
}