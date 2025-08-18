package com.gradation.zmnnoory.domain.datarequest.exception;

import com.gradation.zmnnoory.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class InvalidStatusTransitionException extends BaseException {

    public InvalidStatusTransitionException() {
        super(DataRequestExceptionConstant.INVALID_STATUS_TRANSITION.getMessage());
    }

    @Override
    public HttpStatus getStatus() {
        return DataRequestExceptionConstant.INVALID_STATUS_TRANSITION.getHttpStatus();
    }
}