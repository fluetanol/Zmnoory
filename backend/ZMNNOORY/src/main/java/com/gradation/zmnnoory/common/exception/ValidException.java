package com.gradation.zmnnoory.common.exception;

import org.springframework.http.HttpStatus;

public class ValidException extends BaseException {

    public ValidException() {
        super("요청 데이터에 오류가 있습니다.");
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}
