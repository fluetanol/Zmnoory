package com.gradation.zmnnoory.domain.video.exception;

import com.gradation.zmnnoory.common.exception.BaseException;
import org.springframework.http.HttpStatus;

import static com.gradation.zmnnoory.domain.video.exception.VideoExceptionConstant.VIDEO_ALREADY_UPLOADED_EXCEPTION;

public class VideoAlreadyUploadedException extends BaseException {

    public VideoAlreadyUploadedException(Long videoId) {
        super(VIDEO_ALREADY_UPLOADED_EXCEPTION.getMessage() + " " + videoId);
    }

    public VideoAlreadyUploadedException() {
        super(VIDEO_ALREADY_UPLOADED_EXCEPTION.getMessage());
    }

    @Override
    public HttpStatus getStatus() {
        return VIDEO_ALREADY_UPLOADED_EXCEPTION.getStatus();
    }
}