package com.gradation.zmnnoory.domain.video.dto.response;

import com.gradation.zmnnoory.domain.video.entity.Video;

import java.time.LocalDateTime;

public record VideoDetailResponse(
        Long id,
        Long participationId,
        String title,
        String description,
        Boolean isPublic,
        String videoUrl,
        String thumbnailUrl,
        Boolean isUploaded,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static VideoDetailResponse from(Video video) {
        return new VideoDetailResponse(
                video.getId(),
                video.getParticipation().getId(),
                video.getTitle(),
                video.getDescription(),
                video.isPublic(),
                video.getVideoUrl(),
                video.getThumbnailUrl(),
                video.isVideoUploaded(),
                video.getCreatedAt(),
                video.getUpdatedAt()
        );
    }
}
