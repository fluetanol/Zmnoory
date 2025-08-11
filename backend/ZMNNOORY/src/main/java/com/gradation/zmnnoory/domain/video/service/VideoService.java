    package com.gradation.zmnnoory.domain.video.service;

    import com.gradation.zmnnoory.domain.member.entity.Member;
    import com.gradation.zmnnoory.domain.participation.entity.Participation;
    import com.gradation.zmnnoory.domain.video.dto.request.Base64ImageRequest;
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
        private final VideoImageUploadService videoImageUploadService;

        // 1. 업로드 완료 후 Video 엔티티 생성 (실제 URL과 함께)
        public VideoResponse createVideoWithUploadedData(
                Participation participation, String videoUrl, String thumbnailUrl, 
                String title, String description, Boolean isPublic) {
            
            Video video = Video.builder()
                    .participation(participation)
                    .title(title != null && !title.trim().isEmpty() ? title : "게임 플레이 영상")
                    .description(description != null && !description.trim().isEmpty() ? description : "게임 참여 영상입니다.")
                    .isPublic(isPublic != null ? isPublic : false)
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


        // 3. 현재 로그인 멤버의 모든 비디오 조회
        @Transactional(readOnly = true)
        public List<VideoSummaryResponse> getVideosByMember(Member member) {
            return videoRepository.findAllByParticipation_Member(member).stream()
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

        // 5. 이미지 업로드
        @Transactional
        public void uploadVideoImages(Long videoId, List<Base64ImageRequest> images) {
            Video video = videoRepository.findById(videoId)
                    .orElseThrow(() -> new VideoNotFoundException(videoId));

            Participation participation = video.getParticipation();
            Long userId = participation.getMember().getId();
            Long gameId = participation.getGame().getId();

            videoImageUploadService.uploadBase64Images(userId, gameId, images);
        }


        public void deleteVideo(Participation participation) {
            videoRepository.deleteByParticipation(participation);
        }
    }