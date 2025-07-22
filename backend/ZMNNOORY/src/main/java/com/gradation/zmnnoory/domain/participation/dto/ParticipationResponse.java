package com.gradation.zmnnoory.domain.participation.dto;

import com.gradation.zmnnoory.domain.participation.entity.Participation;
import com.gradation.zmnnoory.domain.participation.status.ParticipationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class ParticipationResponse {

    private final UUID id;
    private final Long memberId;
    private final String memberEmail;
    private final Long stageId;
    private final String stageTitle;
    private final LocalDate startedAt;
    private final LocalDate endedAt;
    private final ParticipationStatus status;
    private final Integer frameCount;
    private final String videoUrl;
    private final String thumbnailUrl;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

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