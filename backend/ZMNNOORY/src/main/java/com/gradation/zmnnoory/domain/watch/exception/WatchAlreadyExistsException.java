package com.gradation.zmnnoory.domain.watch.exception;

import com.gradation.zmnnoory.common.exception.BaseException;
import org.springframework.http.HttpStatus;

import static com.gradation.zmnnoory.domain.watch.exception.WatchExceptionConstant.WATCH_ALREADY_EXISTS_EXCEPTION;

public class WatchAlreadyExistsException extends BaseException {
    
    public WatchAlreadyExistsException(Long videoId, Long memberId) {
        super(WATCH_ALREADY_EXISTS_EXCEPTION.getMessage() + " Video ID: " + videoId + ", Member ID: " + memberId);
    }

    public WatchAlreadyExistsException() {
        super(WATCH_ALREADY_EXISTS_EXCEPTION.getMessage());
    }

    @Override
    public HttpStatus getStatus() {
        return WATCH_ALREADY_EXISTS_EXCEPTION.getStatus();
    }
}
