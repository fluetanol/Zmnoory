package com.gradation.zmnnoory.domain.participation.dto;

import com.gradation.zmnnoory.domain.participation.entity.Participation;
import com.gradation.zmnnoory.domain.participation.status.ParticipationStatus;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;


public class ParticipationResponse {
    private UUID id;
    private Long memberId;
    private String memberEmail;
    private Long stageId;
    private String stageTitle;
    private LocalDate startedAt;
    private LocalDate endedAt;
    private ParticipationStatus status;
    private Integer frameCount;
    private String videoUrl;
    private String thumbnailUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder
    public ParticipationResponse(UUID id, Long memberId, String memberEmail, 
                               Long stageId, String stageTitle, LocalDate startedAt, 
                               LocalDate endedAt, ParticipationStatus status, 
                               Integer frameCount, String videoUrl, String thumbnailUrl,
                               LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.memberId = memberId;
        this.memberEmail = memberEmail;
        this.stageId = stageId;
        this.stageTitle = stageTitle;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
        this.status = status;
        this.frameCount = frameCount;
        this.videoUrl = videoUrl;
        this.thumbnailUrl = thumbnailUrl;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static ParticipationResponse from(Participation participation) {
        return ParticipationResponse.builder()
                .id(participation.getId())
                .memberId(participation.getMember().getId())
                .memberEmail(participation.getMember().getEmail())
                .stageId(participation.getStage().getId())
                .stageTitle(participation.getStage().getTitle())
                .startedAt(participation.getStartedAt())
                .endedAt(participation.getEndedAt())
                .status(participation.getStatus())
                .frameCount(participation.getFrameCount())
                .videoUrl(participation.getVideoUrl())
                .thumbnailUrl(participation.getThumbnailUrl())
                .createdAt(participation.getCreatedAt())
                .updatedAt(participation.getUpdatedAt())
                .build();
    }
}