package com.gradation.zmnnoory.common.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
public class BaseResponse<T> {

    private HttpStatus status;
    private String message;
    private T data;
    private LocalDateTime timestamp;

    private BaseResponse() {
        this.timestamp = LocalDateTime.now();
    }

    @Builder
    private BaseResponse(HttpStatus status, String message, T data) {
        this();
        this.message = message;
        this.status = status;
        this.data = data;
    }

    public static <T> BaseResponse<T> fail(T e, HttpStatus status) {
        return BaseResponse.<T>builder()
                .status(status)
                .data(e)
                .build();
    }

    public static <T> BaseResponse<T> fail(T e, String message, HttpStatus status) {
        return BaseResponse.<T>builder()
                .status(status)
                .message(message)
                .data(e)
                .build();
    }
}
