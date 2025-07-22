package com.gradation.zmnnoory.domain.stage.dto;

import com.gradation.zmnnoory.domain.stage.entity.Stage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class StageResponse {

    private final Long id;
    private final String title;
    private final String description;
    private final String difficulty;
    private final Integer rewardTotal;

    public static StageResponse from(Stage stage) {
        return StageResponse.builder()
                .id(stage.getId())
                .title(stage.getTitle())
                .description(stage.getDescription())
                .difficulty(stage.getDifficulty())
                .rewardTotal(stage.getRewardTotal())
                .build();
    }
}
