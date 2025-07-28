package com.gradation.zmnnoory.domain.member.exception;

import com.gradation.zmnnoory.common.exception.BaseException;
import org.springframework.http.HttpStatus;

import static com.gradation.zmnnoory.domain.member.exception.MemberExceptionConstant.NO_LOGIN_INFO_EXCEPTION;

public class NoLoginInfoException extends BaseException {

    public NoLoginInfoException() {
        super(NO_LOGIN_INFO_EXCEPTION.getMessage());
    }

    @Override
    public HttpStatus getStatus() {
        return NO_LOGIN_INFO_EXCEPTION.getStatus();
    }
}
