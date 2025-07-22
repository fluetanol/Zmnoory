package com.gradation.zmnnoory.domain.stage.dto;

import com.gradation.zmnnoory.domain.stage.entity.Stage;
import lombok.Builder;
import lombok.Getter;

@Getter
public class StageResponse {

    private final String title;
    private final String description;
    private final String difficulty;
    private final Integer rewardTotal;

    @Builder
    private StageResponse(String title, String description, String difficulty, Integer rewardTotal) {
        this.title = title;
        this.description = description;
        this.difficulty = difficulty;
        this.rewardTotal = rewardTotal;
    }

    public static StageResponse of(Stage stage) {
        return StageResponse.builder()
                .title(stage.getTitle())
                .description(stage.getDescription())
                .difficulty(stage.getDifficulty())
                .rewardTotal(stage.getRewardTotal())
                .build();
    }
}
