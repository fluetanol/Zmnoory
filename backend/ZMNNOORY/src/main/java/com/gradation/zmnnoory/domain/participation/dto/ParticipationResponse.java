package com.gradation.zmnnoory.domain.participation.dto;

import com.gradation.zmnnoory.domain.participation.entity.Participation;
import com.gradation.zmnnoory.domain.participation.status.ParticipationStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class ParticipationResponse {
    private final String gameTitle;
    private final LocalDate startedAt;
    private final LocalDate endedAt;
    private final ParticipationStatus status;

    @Builder
    private ParticipationResponse(String gameTitle, LocalDate startedAt,
                               LocalDate endedAt, ParticipationStatus status) {
        this.gameTitle = gameTitle;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
        this.status = status;
    }

    public static ParticipationResponse of(Participation participation) {
        return ParticipationResponse.builder()
                .gameTitle(participation.getGame().getTitle())
                .startedAt(participation.getStartedAt())
                .endedAt(participation.getEndedAt())
                .status(participation.getStatus())
                .build();
    }
}