package com.gradation.zmnnoory.domain.stage.dto;

import com.gradation.zmnnoory.domain.stage.entity.Stage;
import jakarta.validation.constraints.*;
import lombok.Builder;

public class StageRequest {
    
    @NotBlank(message = "제목은 필수입니다")
    @Size(max = 100, message = "제목은 100자 이하여야 합니다")
    private String title;
    
    @NotBlank(message = "설명은 필수입니다")
    @Size(max = 500, message = "설명은 500자 이하여야 합니다")
    private String description;
    
    @NotBlank(message = "난이도는 필수입니다")
    @Pattern(regexp = "^(EASY|MEDIUM|HARD)$", message = "난이도는 EASY, MEDIUM, HARD 중 하나여야 합니다")
    private String difficulty;
    
    @NotNull(message = "보상 포인트는 필수입니다")
    @Positive(message = "보상 포인트는 양수여야 합니다")
    @Max(value = 10000, message = "보상 포인트는 10000 이하여야 합니다")
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