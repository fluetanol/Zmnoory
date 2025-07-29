package com.gradation.zmnnoory.domain.order.exception;

import com.gradation.zmnnoory.common.exception.BaseException;
import org.springframework.http.HttpStatus;

import static com.gradation.zmnnoory.domain.order.exception.OrderExceptionConstant.INSUFFICIENT_POINT_EXCEPTION;


public class InsufficientPointException extends BaseException {

    public InsufficientPointException() { super(INSUFFICIENT_POINT_EXCEPTION.getMessage());}

    @Override
    public HttpStatus getStatus() {return INSUFFICIENT_POINT_EXCEPTION.getStatus();}

}
