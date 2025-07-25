package com.gradation.zmnnoory.domain.participation.dto;

import com.gradation.zmnnoory.domain.participation.entity.Participation;
import com.gradation.zmnnoory.domain.participation.entity.ParticipationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ParticipationResponse {

    @Schema(description = "참여한 게임 제목", example = "도형 맞추기")
    private final String gameTitle;

    @Schema(description = "참여 상태", example = "COMPLETED")
    private final ParticipationStatus status;

    @Builder
    private ParticipationResponse(String gameTitle, ParticipationStatus status) {
        this.gameTitle = gameTitle;
        this.status = status;
    }

    public static ParticipationResponse of(Participation participation) {
        return ParticipationResponse.builder()
                .gameTitle(participation.getGame().getTitle())
                .status(participation.getStatus())
                .build();
    }
}