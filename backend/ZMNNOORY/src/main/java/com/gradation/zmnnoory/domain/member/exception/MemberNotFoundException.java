package com.gradation.zmnnoory.domain.member.exception;

import com.gradation.zmnnoory.common.exception.BaseException;
import org.springframework.http.HttpStatus;

import static com.gradation.zmnnoory.domain.member.exception.MemberExceptionConstant.MEMBER_NOT_FOUND_EXCEPTION;

public class MemberNotFoundException extends BaseException {

    public MemberNotFoundException(String message) {
        super(message);
    }

    public MemberNotFoundException() {
        super(MEMBER_NOT_FOUND_EXCEPTION.getMessage());
    }

    @Override
    public HttpStatus getStatus() {
        return MEMBER_NOT_FOUND_EXCEPTION.getStatus();
    }
}
