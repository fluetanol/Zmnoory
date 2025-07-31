    package com.gradation.zmnnoory.domain.video.service;

    import com.gradation.zmnnoory.domain.participation.entity.Participation;
    import com.gradation.zmnnoory.domain.participation.service.S3Service;
    import com.gradation.zmnnoory.domain.video.dto.response.VideoDetailResponse;
    import com.gradation.zmnnoory.domain.video.dto.response.VideoResponse;
    import com.gradation.zmnnoory.domain.video.dto.response.VideoSummaryResponse;
    import com.gradation.zmnnoory.domain.video.entity.Video;
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

        // 1. 업로드 완료 후 Video 엔티티 생성 (실제 URL과 함께)
        public VideoResponse createVideoWithUploadedData(
                Participation participation, String videoUrl, String thumbnailUrl, 
                String title, String description) {
            
            Video video = Video.builder()
                    .participation(participation)
                    .title(title != null && !title.trim().isEmpty() ? title : "게임 플레이 영상")
                    .description(description != null && !description.trim().isEmpty() ? description : "게임 참여 영상입니다.")
                    .isPublic(false)
                    .videoUrl(videoUrl)
                    .thumbnailUrl(thumbnailUrl != null && !thumbnailUrl.trim().isEmpty() ? thumbnailUrl : "")
                    .build();

            Video savedVideo = videoRepository.save(video);
            log.info("Participation ID: {}에 대한 Video 엔티티 생성 완료. Video ID: {}", 
                     participation.getId(), savedVideo.getId());

            return VideoResponse.of(savedVideo);
        }


        // 2. 영상 단건 조회(영상 상세 페이지)
        @Transactional(readOnly = true)
        public VideoDetailResponse getVideoDetail(Long videoId) {
            Video video = videoRepository.findById(videoId)
                    .orElseThrow(() -> new VideoNotFoundException(videoId));
            return VideoDetailResponse.from(video);
        }


        // 3. 멤버의 업로드 완료된 비디오 목록 조회
        @Transactional(readOnly = true)
        public List<VideoSummaryResponse> getUploadedVideosByMember(Long memberId) {
            return videoRepository.findByParticipationMemberIdAndVideoUrlIsNotNull(memberId).stream()
                    .map(VideoSummaryResponse::from)
                    .toList();
        }

        // 4. 공개된 전체 영상 리스트 조회
        @Transactional(readOnly = true)
        public List<VideoSummaryResponse> getPublicVideos() {
            return videoRepository.findByIsPublicTrueAndVideoUrlIsNotNull().stream()
                    .map(VideoSummaryResponse::from)
                    .toList();
        }

    }