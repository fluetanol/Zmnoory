package com.gradation.zmnnoory.domain.datarequest.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum DataRequestExceptionConstant {

    DATA_REQUEST_NOT_FOUND(HttpStatus.NOT_FOUND, "데이터 요청을 찾을 수 없습니다."),
    INVALID_STATUS_TRANSITION(HttpStatus.BAD_REQUEST, "유효하지 않은 상태 변경입니다.");

    private final HttpStatus httpStatus;
    private final String message;
}