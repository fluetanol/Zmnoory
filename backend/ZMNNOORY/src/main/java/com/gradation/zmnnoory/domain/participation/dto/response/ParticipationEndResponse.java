package com.gradation.zmnnoory.domain.participation.dto.response;

import com.gradation.zmnnoory.domain.participation.entity.Participation;
import com.gradation.zmnnoory.domain.participation.entity.ParticipationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ParticipationEndResponse {

    @Schema(description = "참여한 게임 제목", example = "도형 맞추기")
    private final String gameTitle;

    @Schema(description = "참여 상태", example = "COMPLETED")
    private final ParticipationStatus status;

    @Schema(description = "비디오 아이디", example = "1")
    private final Long videoId;

    @Builder
    private ParticipationEndResponse(String gameTitle, ParticipationStatus status, Long videoId) {
        this.gameTitle = gameTitle;
        this.status = status;
        this.videoId = videoId;
    }

    public static ParticipationEndResponse of(Participation participation, Long videoId) {
        return ParticipationEndResponse.builder()
                .gameTitle(participation.getGame().getTitle())
                .status(participation.getStatus())
                .videoId(videoId)
                .build();
    }
}