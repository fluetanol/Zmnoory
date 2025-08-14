package com.gradation.zmnnoory.domain.giftcard.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum GiftCardExceptionConstant {

    GIFT_CARD_NOT_FOUND("기프티콘을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    GIFT_CARD_ALREADY_EXISTS("이미 등록된 기프티콘 번호입니다.", HttpStatus.CONFLICT),
    NO_AVAILABLE_GIFT_CARD("사용 가능한 기프티콘이 없습니다.", HttpStatus.BAD_REQUEST),
    GIFT_CARD_ALREADY_USED("이미 사용된 기프티콘입니다.", HttpStatus.BAD_REQUEST),
    GIFT_CARD_EXPIRED("만료된 기프티콘입니다.", HttpStatus.BAD_REQUEST),
    GIFT_CARD_NOT_ASSIGNED("할당되지 않은 기프티콘입니다.", HttpStatus.BAD_REQUEST),
    ;

    private final String message;
    private final HttpStatus httpStatus;
}