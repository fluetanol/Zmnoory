package com.gradation.zmnnoory.domain.member.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MemberExceptionConstant {

    DUPLICATED_EMAIL_EXCEPTION("이미 사용 중인 이메일입니다.", HttpStatus.CONFLICT),
    INVALID_GENDER_EXCEPTION("지원하는 성별이 아닙니다.", HttpStatus.BAD_REQUEST),
    ILLEGAL_POINT_USAGE_EXCEPTION("소유한 포인트가 부족합니다.", HttpStatus.BAD_REQUEST),
    MEMBER_NOT_FOUND_EXCEPTION("해당하는 회원이 없습니다.", HttpStatus.NOT_FOUND),
    INVALID_LOGIN_REQUEST_EXCEPTION("이메일 또는 비밀번호가 일치하지 않습니다.", HttpStatus.UNAUTHORIZED),
    INVALID_PASSWORD_EXCEPTION("비밀번호가 일치하지 않습니다.", HttpStatus.UNAUTHORIZED),
    ;

    private final String message;
    private final HttpStatus status;
}
