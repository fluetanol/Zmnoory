package com.gradation.zmnnoory.domain.stage.dto;

import com.gradation.zmnnoory.domain.stage.entity.Stage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StageRequest {
    
    @NotBlank(message = "제목은 필수 값입니다.")
    private final String title;
    
    @NotBlank(message = "설명은 필수 값입니다.")
    private final String description;
    
    @NotBlank(message = "난이도는 필수 값입니다.")
    private final String difficulty;
    
    @NotNull(message = "총 리워드는 필수 값입니다.")
    private final Integer rewardTotal;

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