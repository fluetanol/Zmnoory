package com.gradation.zmnnoory.domain.member.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MemberExceptionConstant {

    DUPLICATED_EMAIL_EXCEPTION("이미 사용 중인 이메일입니다.", HttpStatus.CONFLICT),
    INVALID_GENDER_EXCEPTION("지원하는 성별이 아닙니다.", HttpStatus.BAD_REQUEST);

    private final String message;
    private final HttpStatus status;
}
