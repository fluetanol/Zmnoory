package com.gradation.zmnnoory.domain.video.exception;

import com.gradation.zmnnoory.common.exception.BaseException;
import org.springframework.http.HttpStatus;

import static com.gradation.zmnnoory.domain.video.exception.VideoExceptionConstant.VIDEO_UPLOAD_FAILED_EXCEPTION;

public class VideoUploadFailedException extends BaseException {

    public VideoUploadFailedException(String message) {
        super(message);
    }

    public VideoUploadFailedException() {
        super(VIDEO_UPLOAD_FAILED_EXCEPTION.getMessage());
    }

    @Override
    public HttpStatus getStatus() {
        return VIDEO_UPLOAD_FAILED_EXCEPTION.getStatus();
    }
}