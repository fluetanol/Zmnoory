package com.gradation.zmnnoory.domain.comment.dto.response;

import com.gradation.zmnnoory.domain.comment.entity.Comment;

import java.time.LocalDateTime;

public record CommentResponse(
        String memberNickname,
        String content,
        LocalDateTime createdAt
) {
    public static CommentResponse from(Comment comment) {
        return new CommentResponse(
                comment.getMember().getNickname(),
                comment.getContent(),
                comment.getCreatedAt()
        );
    }
}
