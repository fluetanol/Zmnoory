package com.gradation.zmnnoory.domain.comment.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "댓글 수정 요청 DTO")
public record CommentUpdateRequest(

        @Schema(description = "댓글 내용", example = "수정된 댓글입니다.")
        @NotBlank(message = "댓글 내용은 필수입니다.")
        @Size(max = 500, message = "댓글은 500자 이하로 작성해주세요.")
        String content

) {
}