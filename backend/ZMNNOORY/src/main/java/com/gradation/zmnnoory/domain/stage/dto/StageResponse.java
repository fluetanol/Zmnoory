package com.gradation.zmnnoory.domain.stage.dto;

import com.gradation.zmnnoory.domain.stage.entity.Stage;
import lombok.Builder;
import lombok.Getter;

@Getter
public class StageResponse {
    private Long id;
    private String title;
    private String description;
    private String difficulty;
    private Integer rewardTotal;

    @Builder
    public StageResponse(Long id, String title, String description, String difficulty, Integer rewardTotal) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.difficulty = difficulty;
        this.rewardTotal = rewardTotal;
    }

    // Entity → DTO
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
