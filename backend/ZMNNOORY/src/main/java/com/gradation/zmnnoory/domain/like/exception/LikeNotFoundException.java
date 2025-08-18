package com.gradation.zmnnoory.domain.like.exception;

import com.gradation.zmnnoory.common.exception.BaseException;
import org.springframework.http.HttpStatus;

import static com.gradation.zmnnoory.domain.like.exception.LikeExceptionConstant.LIKE_NOT_FOUND_EXCEPTION;

public class LikeNotFoundException extends BaseException {
    
    public LikeNotFoundException(Long videoId, Long memberId) {
        super(LIKE_NOT_FOUND_EXCEPTION.getMessage() + " Video ID: " + videoId + ", Member ID: " + memberId);
    }

    public LikeNotFoundException() {
        super(LIKE_NOT_FOUND_EXCEPTION.getMessage());
    }

    @Override
    public HttpStatus getStatus() {
        return LIKE_NOT_FOUND_EXCEPTION.getStatus();
    }
}