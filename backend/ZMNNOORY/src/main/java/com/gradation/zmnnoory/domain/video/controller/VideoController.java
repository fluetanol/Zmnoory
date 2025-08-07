package com.gradation.zmnnoory.domain.video.controller;

import com.gradation.zmnnoory.common.dto.BaseResponse;
import com.gradation.zmnnoory.domain.member.annotation.LoginMember;
import com.gradation.zmnnoory.domain.member.entity.Member;
import com.gradation.zmnnoory.domain.participation.repository.ParticipationRepository;
import com.gradation.zmnnoory.domain.video.dto.request.VideoImageUploadRequest;
import com.gradation.zmnnoory.domain.video.dto.response.VideoDetailResponse;
import com.gradation.zmnnoory.domain.video.dto.response.VideoSummaryResponse;
import com.gradation.zmnnoory.domain.video.repository.VideoRepository;
import com.gradation.zmnnoory.domain.video.service.VideoImageUploadService;
import com.gradation.zmnnoory.domain.video.service.VideoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "비디오 API", description = "비디오 업로드, 정보 수정, 조회 기능")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/videos")
@Validated
public class VideoController {

    private final VideoService videoService;
    private final ParticipationRepository participationRepository;
    private final VideoRepository videoRepository;
    private final VideoImageUploadService videoImageUploadService;

    // 1. 비디오 상세 페이지
    @Operation(summary = "비디오 상세 조회", description = "비디오 ID를 통해 상세 정보를 조회합니다.")
    @GetMapping("/{videoId}")
    public BaseResponse<VideoDetailResponse> getVideoDetail(@PathVariable Long videoId) {
        return BaseResponse.<VideoDetailResponse>builder()
                .status(HttpStatus.OK)
                .data(videoService.getVideoDetail(videoId))
                .build();
    }

    // 2. 멤버의 업로드 완료된 비디오 목록 조회
    @Operation(
            summary = "멤버별 업로드 완료 비디오 목록 조회",
            description = """
            특정 멤버가 업로드 완료한 모든 비디오를 조회합니다.
            - 업로드가 완료된 비디오만 반환합니다 (videoUrl이 존재하는 비디오).
            """
    )
    @GetMapping("/member/{memberId}")
    public BaseResponse<List<VideoSummaryResponse>> getUploadedVideosByMember(
            @PathVariable Long memberId) {

        return BaseResponse.<List<VideoSummaryResponse>>builder()
                .status(HttpStatus.OK)
                .data(videoService.getUploadedVideosByMember(memberId))
                .build();
    }

    // 3.  공개된 전체 영상 리스트 조회
    @Operation(
            summary = "공개된 전체 영상 리스트 조회",
            description = """
            공개 설정된 모든 비디오를 조회합니다.
            - isPublic이 true이고 videoUrl이 존재하는 비디오만 반환합니다.
            """
    )
    @GetMapping("/public")
    public BaseResponse<List<VideoSummaryResponse>> getPublicVideos() {

        return BaseResponse.<List<VideoSummaryResponse>>builder()
                .status(HttpStatus.OK)
                .data(videoService.getPublicVideos())
                .build();
    }

    // 4. 이미지 업로드
    @Operation(
            summary = "비디오 관련 이미지 업로드",
            description = """
            비디오에 연관된 이미지들을 Base64 형식으로 업로드합니다.
            - 요청 본문에는 비디오 ID와 Base64 인코딩된 이미지 리스트가 포함되어야 합니다.
            - 이미지들은 S3에 저장되며, 고유한 파일명(UUID 기반)으로 업로드됩니다.
            - 업로드는 모두 성공해야 하며, 실패 시 전체 트랜잭션이 롤백됩니다.
            """
    )
    @PostMapping("/images")
    public ResponseEntity<Void> uploadVideoImages(
            @RequestBody VideoImageUploadRequest request) {

        videoService.uploadVideoImages(request.videoId(), request.images());
        return ResponseEntity.ok().build();
    }

    // 5. 현재 로그인 사용자의 모든 비디오 조회
    @Operation(
            summary = "내 업로드 완료 비디오 목록 조회",
            description = """
            로그인한 사용자가 업로드를 완료한 비디오 목록을 조회합니다.
            - videoUrl이 존재하는 (즉, 실제 업로드가 완료된) 비디오만 반환합니다.
            """
    )
    @GetMapping("/my")
    public BaseResponse<List<VideoSummaryResponse>> getMyUploadedVideos(@LoginMember Member member) {
        return BaseResponse.<List<VideoSummaryResponse>>builder()
                .status(HttpStatus.OK)
                .data(videoService.getUploadedVideosByMember(member.getId()))
                .build();
    }
}