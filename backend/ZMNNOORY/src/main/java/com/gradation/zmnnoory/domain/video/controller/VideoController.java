    package com.gradation.zmnnoory.domain.video.controller;

    import com.gradation.zmnnoory.common.dto.BaseResponse;
    import com.gradation.zmnnoory.domain.video.dto.response.VideoDetailResponse;
    import com.gradation.zmnnoory.domain.video.dto.response.VideoSummaryResponse;
    import com.gradation.zmnnoory.domain.video.service.VideoService;
    import io.swagger.v3.oas.annotations.Operation;
    import io.swagger.v3.oas.annotations.tags.Tag;
    import lombok.RequiredArgsConstructor;
    import org.springframework.http.HttpStatus;
    import org.springframework.validation.annotation.Validated;
    import org.springframework.web.bind.annotation.GetMapping;
    import org.springframework.web.bind.annotation.PathVariable;
    import org.springframework.web.bind.annotation.RequestMapping;
    import org.springframework.web.bind.annotation.RestController;

    import java.util.List;

    @Tag(name = "비디오 API", description = "비디오 업로드, 정보 수정, 조회 기능")
    @RestController
    @RequiredArgsConstructor
    @RequestMapping("/api/videos")
    @Validated
    public class VideoController {

        private final VideoService videoService;


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
    }