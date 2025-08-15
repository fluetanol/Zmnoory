package com.gradation.zmnnoory.domain.datarequest.exception;

import com.gradation.zmnnoory.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class DataRequestNotFoundException extends BaseException {

    public DataRequestNotFoundException() {
        super(DataRequestExceptionConstant.DATA_REQUEST_NOT_FOUND.getMessage());
    }

    @Override
    public HttpStatus getStatus() {
        return DataRequestExceptionConstant.DATA_REQUEST_NOT_FOUND.getHttpStatus();
    }
}