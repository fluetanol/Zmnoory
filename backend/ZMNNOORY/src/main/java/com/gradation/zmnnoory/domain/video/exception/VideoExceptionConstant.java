package com.gradation.zmnnoory.domain.video.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum VideoExceptionConstant {

    VIDEO_NOT_FOUND_EXCEPTION("비디오를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

    VIDEO_ALREADY_UPLOADED_EXCEPTION("이미 업로드된 비디오입니다.", HttpStatus.CONFLICT),

    VIDEO_UPLOAD_FAILED_EXCEPTION("비디오 업로드에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String message;

    private final HttpStatus status;
}