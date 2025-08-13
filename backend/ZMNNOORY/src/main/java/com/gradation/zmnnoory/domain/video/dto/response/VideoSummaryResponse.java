package com.gradation.zmnnoory.domain.video.dto.response;

import com.gradation.zmnnoory.domain.video.entity.Video;

import java.time.LocalDateTime;

public record VideoSummaryResponse(
        Long id,
        Long participationId,
        String memberNickname,
        String memberProfileImageUrl,
        String gameTitle,
        String title,
        String description,
        Boolean isPublic,
        String videoUrl,
        String thumbnailUrl,
        LocalDateTime createdAt
) {

    public static VideoSummaryResponse from(Video video) {
        return new VideoSummaryResponse(
                video.getId(),
                video.getParticipation().getId(),
                video.getParticipation().getMember().getNickname(),
                video.getParticipation().getMember().getProfileImageUrl(),
                video.getParticipation().getGame().getTitle(),
                video.getTitle(),
                video.getDescription(),
                video.isPublic(),
                video.getVideoUrl(),
                video.getThumbnailUrl(),
                video.getCreatedAt()
        );
    }
}