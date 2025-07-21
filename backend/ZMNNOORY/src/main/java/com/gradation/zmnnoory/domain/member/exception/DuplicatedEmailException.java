package com.gradation.zmnnoory.domain.member.exception;

import com.gradation.zmnnoory.common.exception.BaseException;
import org.springframework.http.HttpStatus;

import static com.gradation.zmnnoory.domain.member.exception.MemberExceptionConstant.*;

public class DuplicatedEmailException extends BaseException {

    public DuplicatedEmailException() {
        super(DUPLICATED_EMAIL_EXCEPTION.getMessage());
    }

    @Override
    public HttpStatus getStatus() {
        return DUPLICATED_EMAIL_EXCEPTION.getStatus();
    }
}
