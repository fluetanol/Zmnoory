package com.gradation.zmnnoory.domain.comment.controller;

import com.gradation.zmnnoory.common.dto.BaseResponse;
import com.gradation.zmnnoory.domain.comment.dto.request.CommentCreateRequest;
import com.gradation.zmnnoory.domain.comment.dto.request.CommentUpdateRequest;
import com.gradation.zmnnoory.domain.comment.dto.response.CommentResponse;
import com.gradation.zmnnoory.domain.comment.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "댓글 API", description = "비디오 댓글 관리 기능")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comments")
@Validated
public class CommentController {

    private final CommentService commentService;

    @Operation(
            summary = "댓글 작성",
            description = """
            특정 비디오에 댓글을 작성합니다.
            - 로그인한 사용자만 댓글을 작성할 수 있습니다.
            - 댓글은 최대 500자까지 작성 가능합니다.
            """
    )
    @PostMapping("/videos/{videoId}")
    public BaseResponse<CommentResponse> createComment(
            @PathVariable Long videoId,
            @RequestParam Long memberId,
            @Valid @RequestBody CommentCreateRequest request) {

        return BaseResponse.<CommentResponse>builder()
                .status(HttpStatus.CREATED)
                .data(commentService.createComment(videoId, memberId, request))
                .build();
    }

    @Operation(
            summary = "비디오 댓글 목록 조회",
            description = """
            특정 비디오의 모든 댓글을 조회합니다.
            - 댓글은 작성 시간 순으로 정렬됩니다.
            - 작성자 정보도 함께 반환됩니다.
            """
    )
    @GetMapping("/videos/{videoId}")
    public BaseResponse<List<CommentResponse>> getCommentsByVideo(@PathVariable Long videoId) {
        return BaseResponse.<List<CommentResponse>>builder()
                .status(HttpStatus.OK)
                .data(commentService.getCommentsByVideo(videoId))
                .build();
    }

    @Operation(
            summary = "댓글 수정",
            description = """
            자신이 작성한 댓글을 수정합니다.
            - 댓글 작성자만 수정할 수 있습니다.
            - 댓글은 최대 500자까지 작성 가능합니다.
            """
    )
    @PutMapping("/{commentId}")
    public BaseResponse<CommentResponse> updateComment(
            @PathVariable Long commentId,
            @RequestParam Long memberId,
            @Valid @RequestBody CommentUpdateRequest request) {

        return BaseResponse.<CommentResponse>builder()
                .status(HttpStatus.OK)
                .data(commentService.updateComment(commentId, memberId, request))
                .build();
    }

    @Operation(
            summary = "댓글 삭제",
            description = """
            자신이 작성한 댓글을 삭제합니다.
            - 댓글 작성자만 삭제할 수 있습니다.
            """
    )
    @DeleteMapping("/{commentId}")
    public BaseResponse<Void> deleteComment(
            @PathVariable Long commentId,
            @RequestParam Long memberId) {

        commentService.deleteComment(commentId, memberId);
        return BaseResponse.<Void>builder()
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @Operation(
            summary = "비디오 댓글 수 조회",
            description = "특정 비디오의 총 댓글 수를 조회합니다."
    )
    @GetMapping("/videos/{videoId}/count")
    public BaseResponse<Long> getCommentCount(@PathVariable Long videoId) {
        return BaseResponse.<Long>builder()
                .status(HttpStatus.OK)
                .data(commentService.getCommentCount(videoId))
                .build();
    }
}
