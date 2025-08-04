package com.gradation.zmnnoory.domain.watch.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum WatchExceptionConstant {
    WATCH_ALREADY_EXISTS_EXCEPTION("이미 시청한 비디오입니다.", HttpStatus.CONFLICT);

    private final String message;
    private final HttpStatus status;
}
