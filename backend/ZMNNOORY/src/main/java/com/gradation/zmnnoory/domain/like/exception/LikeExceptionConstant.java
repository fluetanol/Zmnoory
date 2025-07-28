package com.gradation.zmnnoory.domain.like.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum LikeExceptionConstant {
    LIKE_ALREADY_EXISTS_EXCEPTION("이미 좋아요를 누른 비디오입니다.", HttpStatus.CONFLICT),
    LIKE_NOT_FOUND_EXCEPTION("좋아요를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);

    private final String message;
    private final HttpStatus status;
}