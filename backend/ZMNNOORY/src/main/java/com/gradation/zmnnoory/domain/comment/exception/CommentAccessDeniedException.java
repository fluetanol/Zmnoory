package com.gradation.zmnnoory.domain.comment.exception;

import com.gradation.zmnnoory.common.exception.BaseException;
import org.springframework.http.HttpStatus;

import static com.gradation.zmnnoory.domain.comment.exception.CommentExceptionConstant.COMMENT_ACCESS_DENIED_EXCEPTION;

public class CommentAccessDeniedException extends BaseException {
    
    public CommentAccessDeniedException(Long commentId) {
        super(COMMENT_ACCESS_DENIED_EXCEPTION.getMessage() + " " + commentId);
    }

    public CommentAccessDeniedException() {
        super(COMMENT_ACCESS_DENIED_EXCEPTION.getMessage());
    }

    @Override
    public HttpStatus getStatus() {
        return COMMENT_ACCESS_DENIED_EXCEPTION.getStatus();
    }
}