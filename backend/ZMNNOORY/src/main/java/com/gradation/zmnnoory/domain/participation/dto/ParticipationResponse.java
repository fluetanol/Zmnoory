package com.gradation.zmnnoory.domain.participation.dto;

import com.gradation.zmnnoory.domain.participation.entity.Participation;
import com.gradation.zmnnoory.domain.participation.status.ParticipationStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class ParticipationResponse {
    private final String stageTitle;
    private final LocalDate startedAt;
    private final LocalDate endedAt;
    private final ParticipationStatus status;

    @Builder
    private ParticipationResponse(String stageTitle, LocalDate startedAt,
                               LocalDate endedAt, ParticipationStatus status) {
        this.stageTitle = stageTitle;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
        this.status = status;
    }

    public static ParticipationResponse of(Participation participation) {
        return ParticipationResponse.builder()
                .stageTitle(participation.getStage().getTitle())
                .startedAt(participation.getStartedAt())
                .endedAt(participation.getEndedAt())
                .status(participation.getStatus())
                .build();
    }
}