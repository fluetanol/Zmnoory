package com.gradation.zmnnoory.domain.member.exception;

import com.gradation.zmnnoory.common.exception.BaseException;
import org.springframework.http.HttpStatus;

import static com.gradation.zmnnoory.domain.member.exception.MemberExceptionConstant.ILLEGAL_POINT_USAGE_EXCEPTION;

public class IllegalPointUsageException extends BaseException {

    public IllegalPointUsageException() {
        super(ILLEGAL_POINT_USAGE_EXCEPTION.getMessage());
    }

    @Override
    public HttpStatus getStatus() {
        return ILLEGAL_POINT_USAGE_EXCEPTION.getStatus();
    }
}
