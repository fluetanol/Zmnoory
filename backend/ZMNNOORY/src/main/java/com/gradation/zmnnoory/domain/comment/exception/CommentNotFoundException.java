package com.gradation.zmnnoory.domain.comment.exception;

import com.gradation.zmnnoory.common.exception.BaseException;
import org.springframework.http.HttpStatus;

import static com.gradation.zmnnoory.domain.comment.exception.CommentExceptionConstant.COMMENT_NOT_FOUND_EXCEPTION;

public class CommentNotFoundException extends BaseException {
    
    public CommentNotFoundException(Long commentId) {
        super(COMMENT_NOT_FOUND_EXCEPTION.getMessage() + " " + commentId);
    }

    public CommentNotFoundException() {
        super(COMMENT_NOT_FOUND_EXCEPTION.getMessage());
    }

    @Override
    public HttpStatus getStatus() {
        return COMMENT_NOT_FOUND_EXCEPTION.getStatus();
    }
}