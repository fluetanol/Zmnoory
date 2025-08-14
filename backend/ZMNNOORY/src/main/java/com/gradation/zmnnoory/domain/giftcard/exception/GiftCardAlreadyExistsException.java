package com.gradation.zmnnoory.domain.giftcard.exception;

import com.gradation.zmnnoory.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class GiftCardAlreadyExistsException extends BaseException {

    public GiftCardAlreadyExistsException() {
        super(GiftCardExceptionConstant.GIFT_CARD_ALREADY_EXISTS.getMessage());
    }

    @Override
    public HttpStatus getStatus() {
        return GiftCardExceptionConstant.GIFT_CARD_ALREADY_EXISTS.getHttpStatus();
    }
}