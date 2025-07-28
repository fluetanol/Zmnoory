package com.gradation.zmnnoory.domain.like.exception;

import com.gradation.zmnnoory.common.exception.BaseException;
import org.springframework.http.HttpStatus;

import static com.gradation.zmnnoory.domain.like.exception.LikeExceptionConstant.LIKE_ALREADY_EXISTS_EXCEPTION;

public class LikeAlreadyExistsException extends BaseException {
    
    public LikeAlreadyExistsException(Long videoId, Long memberId) {
        super(LIKE_ALREADY_EXISTS_EXCEPTION.getMessage() + " Video ID: " + videoId + ", Member ID: " + memberId);
    }

    public LikeAlreadyExistsException() {
        super(LIKE_ALREADY_EXISTS_EXCEPTION.getMessage());
    }

    @Override
    public HttpStatus getStatus() {
        return LIKE_ALREADY_EXISTS_EXCEPTION.getStatus();
    }
}