package com.gradation.zmnnoory.domain.video.exception;

import com.gradation.zmnnoory.common.exception.BaseException;
import org.springframework.http.HttpStatus;

import static com.gradation.zmnnoory.domain.video.exception.VideoExceptionConstant.VIDEO_NOT_FOUND_EXCEPTION;

public class VideoNotFoundException extends BaseException {

    public VideoNotFoundException(Long videoId) {
        super(VIDEO_NOT_FOUND_EXCEPTION.getMessage() + " " + videoId);
    }

    public VideoNotFoundException(String message) {
        super(message);
    }

    public VideoNotFoundException() {
        super(VIDEO_NOT_FOUND_EXCEPTION.getMessage());
    }

    @Override
    public HttpStatus getStatus() {
        return VIDEO_NOT_FOUND_EXCEPTION.getStatus();
    }
}
