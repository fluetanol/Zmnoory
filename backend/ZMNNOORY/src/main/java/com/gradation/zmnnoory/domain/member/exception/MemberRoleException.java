package com.gradation.zmnnoory.domain.member.exception;

import com.gradation.zmnnoory.common.exception.BaseException;
import org.springframework.http.HttpStatus;

import static com.gradation.zmnnoory.domain.member.exception.MemberExceptionConstant.MEMBER_ROLE_EXCEPTION;

public class MemberRoleException extends BaseException {

    public MemberRoleException() {
        super(MEMBER_ROLE_EXCEPTION.getMessage());
    }

    @Override
    public HttpStatus getStatus() {
        return MEMBER_ROLE_EXCEPTION.getStatus();
    }
}
