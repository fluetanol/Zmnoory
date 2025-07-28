package com.gradation.zmnnoory.domain.comment.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CommentExceptionConstant {
    COMMENT_NOT_FOUND_EXCEPTION("댓글을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    COMMENT_ACCESS_DENIED_EXCEPTION("댓글에 대한 권한이 없습니다.", HttpStatus.FORBIDDEN);

    private final String message;
    private final HttpStatus status;
}