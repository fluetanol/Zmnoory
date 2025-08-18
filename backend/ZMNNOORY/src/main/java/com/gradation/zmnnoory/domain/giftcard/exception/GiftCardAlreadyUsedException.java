package com.gradation.zmnnoory.domain.giftcard.exception;

import com.gradation.zmnnoory.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class GiftCardAlreadyUsedException extends BaseException {

    public GiftCardAlreadyUsedException() {
        super(GiftCardExceptionConstant.GIFT_CARD_ALREADY_USED.getMessage());
    }

    @Override
    public HttpStatus getStatus() {
        return GiftCardExceptionConstant.GIFT_CARD_ALREADY_USED.getHttpStatus();
    }
}