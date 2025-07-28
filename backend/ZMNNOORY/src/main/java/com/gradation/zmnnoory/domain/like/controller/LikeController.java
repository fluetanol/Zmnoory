package com.gradation.zmnnoory.domain.like.controller;

import com.gradation.zmnnoory.common.dto.BaseResponse;
import com.gradation.zmnnoory.domain.like.dto.response.LikeResponse;
import com.gradation.zmnnoory.domain.like.service.LikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "좋아요 API", description = "비디오 좋아요 관리 기능")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/likes")
@Validated
public class LikeController {

    private final LikeService likeService;

    @Operation(
            summary = "좋아요 토글",
            description = """
            비디오에 좋아요를 추가하거나 취소합니다.
            - 이미 좋아요를 누른 경우 취소됩니다.
            - 좋아요를 누르지 않은 경우 추가됩니다.
            - 현재 좋아요 상태와 총 좋아요 수를 반환합니다.
            """
    )
    @PostMapping("/videos/{videoId}")
    public BaseResponse<LikeResponse> toggleLike(
            @PathVariable Long videoId,
            @RequestParam Long memberId) {

        return BaseResponse.<LikeResponse>builder()
                .status(HttpStatus.OK)
                .data(likeService.toggleLike(videoId, memberId))
                .build();
    }

    @Operation(
            summary = "좋아요 상태 조회",
            description = """
            특정 사용자의 비디오 좋아요 상태를 조회합니다.
            - 사용자가 해당 비디오에 좋아요를 눌렀는지 여부를 반환합니다.
            - 비디오의 총 좋아요 수도 함께 반환합니다.
            """
    )
    @GetMapping("/videos/{videoId}")
    public BaseResponse<LikeResponse> getLikeStatus(
            @PathVariable Long videoId,
            @RequestParam Long memberId) {

        return BaseResponse.<LikeResponse>builder()
                .status(HttpStatus.OK)
                .data(likeService.getLikeStatus(videoId, memberId))
                .build();
    }

    @Operation(
            summary = "비디오 좋아요 수 조회",
            description = "특정 비디오의 총 좋아요 수를 조회합니다."
    )
    @GetMapping("/videos/{videoId}/count")
    public BaseResponse<Long> getLikeCount(@PathVariable Long videoId) {
        return BaseResponse.<Long>builder()
                .status(HttpStatus.OK)
                .data(likeService.getLikeCount(videoId))
                .build();
    }
}
