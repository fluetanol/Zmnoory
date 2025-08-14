package com.gradation.zmnnoory.domain.giftcard.exception;

import com.gradation.zmnnoory.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class NoAvailableGiftCardException extends BaseException {

    public NoAvailableGiftCardException() {
        super(GiftCardExceptionConstant.NO_AVAILABLE_GIFT_CARD.getMessage());
    }

    @Override
    public HttpStatus getStatus() {
        return GiftCardExceptionConstant.NO_AVAILABLE_GIFT_CARD.getHttpStatus();
    }
}