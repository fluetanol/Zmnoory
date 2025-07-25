package com.gradation.zmnnoory.domain.member.exception;

import com.gradation.zmnnoory.common.exception.BaseException;
import org.springframework.http.HttpStatus;

import static com.gradation.zmnnoory.domain.member.exception.MemberExceptionConstant.INVALID_LOGIN_REQUEST_EXCEPTION;

public class InvalidLoginRequestException extends BaseException {

    public InvalidLoginRequestException() {
        super(INVALID_LOGIN_REQUEST_EXCEPTION.getMessage());
    }

    @Override
    public HttpStatus getStatus() {
        return INVALID_LOGIN_REQUEST_EXCEPTION.getStatus();
    }
}
