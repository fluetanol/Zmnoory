package com.gradation.zmnnoory.domain.comment.dto.response;

import com.gradation.zmnnoory.domain.comment.entity.Comment;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;


@Getter
public class CommentResponse {

    @Schema(description = "사용자 닉네임", example = "9wan9hyeon")
    private final String memberNickname;

    @Schema(description = "사용자 프로필 이미지 URL", example = "https://example.com/profile.jpg")
    private final String memberProfileImageUrl;

    @Schema(description = "댓글 내용", example = "첫 댓글입니다.")
    private final String content;

    @Schema(description = "댓글 작성 시간", example = "2025.05.05")
    private final LocalDateTime createdAt;

    @Builder
    private CommentResponse(String memberNickname, String memberProfileImageUrl, String content, LocalDateTime createdAt) {
        this.memberNickname = memberNickname;
        this.memberProfileImageUrl = memberProfileImageUrl;
        this.content = content;
        this.createdAt = createdAt;
    }

    public static CommentResponse of(Comment comment) {
        return CommentResponse.builder()
                .memberNickname(comment.getMember().getNickname())
                .memberProfileImageUrl(comment.getMember().getProfileImageUrl())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .build();
    }

}