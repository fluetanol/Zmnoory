    package com.gradation.zmnnoory.domain.video.service;

    import com.gradation.zmnnoory.domain.participation.entity.Participation;
    import com.gradation.zmnnoory.domain.video.dto.request.VideoUpdateRequest;
    import com.gradation.zmnnoory.domain.video.dto.response.PreSignedUrlResponse;
    import com.gradation.zmnnoory.domain.video.dto.response.VideoDetailResponse;
    import com.gradation.zmnnoory.domain.video.dto.response.VideoResponse;
    import com.gradation.zmnnoory.domain.video.dto.response.VideoSummaryResponse;
    import com.gradation.zmnnoory.domain.video.entity.Video;
    import com.gradation.zmnnoory.domain.video.exception.VideoAlreadyUploadedException;
    import com.gradation.zmnnoory.domain.video.exception.VideoNotFoundException;
    import com.gradation.zmnnoory.domain.video.repository.VideoRepository;
    import lombok.RequiredArgsConstructor;
    import lombok.extern.slf4j.Slf4j;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;

    import java.util.List;

    @Slf4j
    @Service
    @RequiredArgsConstructor
    @Transactional
    public class VideoService {

        private final VideoRepository videoRepository;
        private final S3Service s3Service;

        // 1. participation complete면 바로 video 생성(자동)
        public VideoResponse createVideoForParticipation(Participation participation) {
            Video video = Video.builder()
                    .participation(participation)
                    .isPublic(false)
                    .build();

            Video savedVideo = videoRepository.save(video);
            log.info("Participation ID: {}에 대한 Video 엔티티 생성 완료. Video ID: {}",
                     participation.getId(), savedVideo.getId());

            return VideoResponse.of(savedVideo);
        }

        // 2. S3 Pre-signed URL 생성
        public PreSignedUrlResponse generateUploadUrl(Long videoId, String fileName, String contentType) {
            Video video = videoRepository.findById(videoId)
                    .orElseThrow(() -> new VideoNotFoundException(videoId));

            if (video.isVideoUploaded()) {
                throw new VideoAlreadyUploadedException(videoId);
            }

            Long userId = video.getParticipation().getMember().getId();
            Long gameId = video.getParticipation().getGame().getId();

            return s3Service.generatePreSignedUrl(videoId, userId, gameId, fileName, contentType);
        }

        // 3. 프론트에서 업로드 완료 후 비디오 메타 데이터 업데이트
        public VideoResponse updateVideoInfo(Long videoId, VideoUpdateRequest request) {
            Video video = videoRepository.findById(videoId)
                    .orElseThrow(() -> new VideoNotFoundException(videoId));

            video.updateVideoInfo(
                    request.title(),
                    request.description(),
                    request.isPublic(),
                    request.videoUrl(),
                    request.thumbnailUrl()
            );

            log.info("Video ID: {} 정보 업데이트 완료. 업로드 상태: {}",
                     videoId, video.isVideoUploaded());

            return VideoResponse.of(video);
        }

        // 4. 영상 단건 조회(영상 상세 페이지)
        @Transactional(readOnly = true)
        public VideoDetailResponse getVideoDetail(Long videoId) {
            Video video = videoRepository.findById(videoId)
                    .orElseThrow(() -> new VideoNotFoundException(videoId));
            return VideoDetailResponse.from(video);
        }


        // 5. 멤버의 업로드 완료된 비디오 목록 조회
        @Transactional(readOnly = true)
        public List<VideoSummaryResponse> getUploadedVideosByMember(Long memberId) {
            return videoRepository.findByParticipationMemberIdAndVideoUrlIsNotNull(memberId).stream()
                    .map(VideoSummaryResponse::from)
                    .toList();
        }

        // 6. 공개된 전체 영상 리스트 조회
        @Transactional(readOnly = true)
        public List<VideoSummaryResponse> getPublicVideos() {
            return videoRepository.findByIsPublicTrueAndVideoUrlIsNotNull().stream()
                    .map(VideoSummaryResponse::from)
                    .toList();
        }

    }