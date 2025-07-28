    package com.gradation.zmnnoory.domain.video.controller;

    import com.gradation.zmnnoory.common.dto.BaseResponse;
    import com.gradation.zmnnoory.domain.video.dto.request.VideoUpdateRequest;
    import com.gradation.zmnnoory.domain.video.dto.response.PreSignedUrlResponse;
    import com.gradation.zmnnoory.domain.video.dto.response.VideoDetailResponse;
    import com.gradation.zmnnoory.domain.video.dto.response.VideoResponse;
    import com.gradation.zmnnoory.domain.video.dto.response.VideoSummaryResponse;
    import com.gradation.zmnnoory.domain.video.service.VideoService;
    import io.swagger.v3.oas.annotations.Operation;
    import io.swagger.v3.oas.annotations.tags.Tag;
    import jakarta.validation.Valid;
    import lombok.RequiredArgsConstructor;
    import org.springframework.http.HttpStatus;
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

        // 1. S3 Pre-signed URL 생성
        @Operation(
                summary = "S3 Pre-signed URL 생성",
                description = """
                비디오 업로드를 위한 S3 Pre-signed URL을 생성합니다.
                - 15분간 유효한 업로드 URL을 반환합니다.
                - 이미 업로드된 비디오의 경우 예외가 발생합니다.
                """
        )
        @PostMapping("/{videoId}/upload-url")
        public BaseResponse<PreSignedUrlResponse> generateUploadUrl(
                @PathVariable Long videoId,
                @RequestParam String fileName,
                @RequestParam(defaultValue = "video/mp4") String contentType) {

            PreSignedUrlResponse response = videoService.generateUploadUrl(videoId, fileName, contentType);

            return BaseResponse.<PreSignedUrlResponse>builder()
                    .status(HttpStatus.OK)
                    .data(response)
                    .build();
        }

        // 2. 비디오 정보 업데이트 (업로드 완료 후)
        @Operation(
                summary = "비디오 정보 업데이트",
                description = """
                프론트엔드에서 S3 업로드 완료 후 비디오 정보를 업데이트합니다.
                - 비디오 URL, 썸네일 URL, 제목, 설명, 공개 여부를 설정할 수 있습니다.
                - 업로드 완료 처리를 위해 videoUrl은 필수입니다.
                """
        )
        @PutMapping("/{videoId}")
        public BaseResponse<VideoResponse> updateVideoInfo(
                @PathVariable Long videoId,
                @Valid @RequestBody VideoUpdateRequest request) {

            return BaseResponse.<VideoResponse>builder()
                    .status(HttpStatus.OK)
                    .data(videoService.updateVideoInfo(videoId, request))
                    .build();
        }

        // 3. 비디오 상세 페이지
        @Operation(summary = "비디오 상세 조회", description = "비디오 ID를 통해 상세 정보를 조회합니다.")
        @GetMapping("/{videoId}")
        public BaseResponse<VideoDetailResponse> getVideoDetail(@PathVariable Long videoId) {
            return BaseResponse.<VideoDetailResponse>builder()
                    .status(HttpStatus.OK)
                    .data(videoService.getVideoDetail(videoId))
                    .build();
        }

        // 4. 멤버의 업로드 완료된 비디오 목록 조회
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

        // 5.  공개된 전체 영상 리스트 조회
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