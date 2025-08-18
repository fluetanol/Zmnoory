package com.gradation.zmnnoory.domain.order.exception;

import com.gradation.zmnnoory.common.exception.BaseException;
import org.springframework.http.HttpStatus;

import static com.gradation.zmnnoory.domain.order.exception.OrderExceptionConstant.PRODUCT_NOT_FOUND_EXCEPTION;


public class ProductNotFoundException extends BaseException {

    public ProductNotFoundException() { super(PRODUCT_NOT_FOUND_EXCEPTION.getMessage());}

    @Override
    public HttpStatus getStatus() {return PRODUCT_NOT_FOUND_EXCEPTION.getStatus();}

}