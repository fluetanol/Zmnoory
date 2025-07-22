package com.gradation.zmnnoory.domain.stage.dto;


import com.gradation.zmnnoory.domain.stage.entity.Stage;
import lombok.Builder;


public class StageRequest {
    private String title;
    private String description;
    private String difficulty;
    private Integer rewardTotal;

    @Builder
    public StageRequest(String title, String description, String difficulty, Integer rewardTotal) {
        this.title = title;
        this.description = description;
        this.difficulty = difficulty;
        this.rewardTotal = rewardTotal;
    }

    // DTO → Entity
    public Stage toEntity() {
        return Stage.builder()
                .title(this.title)
                .description(this.description)
                .difficulty(this.difficulty)
                .rewardTotal(this.rewardTotal)
                .build();
    }
}