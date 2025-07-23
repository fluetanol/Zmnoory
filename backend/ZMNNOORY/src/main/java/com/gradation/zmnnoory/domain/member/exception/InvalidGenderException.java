package com.gradation.zmnnoory.domain.member.exception;

import com.gradation.zmnnoory.common.exception.BaseException;
import org.springframework.http.HttpStatus;

import static com.gradation.zmnnoory.domain.member.exception.MemberExceptionConstant.INVALID_GENDER_EXCEPTION;

public class InvalidGenderException extends BaseException {

    public InvalidGenderException(String value) {
        super(INVALID_GENDER_EXCEPTION.getMessage());
        addValidation("gender", value);
    }

    @Override
    public HttpStatus getStatus() {
        return INVALID_GENDER_EXCEPTION.getStatus();
    }
}
