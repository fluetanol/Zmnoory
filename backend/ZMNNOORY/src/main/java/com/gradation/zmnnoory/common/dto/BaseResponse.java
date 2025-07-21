package com.gradation.zmnnoory.common.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
public class BaseResponse<T> {

    private HttpStatus status;
    private T data;
    private LocalDateTime timestamp;

    private BaseResponse() {
        this.timestamp = LocalDateTime.now();
    }

    @Builder
    private BaseResponse(HttpStatus status,T data) {
        this();
        this.status = status;
        this.data = data;
    }
}
