package com.gradation.zmnnoory.domain.participation.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UpdateParticipationRequest {
    
    @NotNull(message = "프레임 수는 필수입니다")
    @PositiveOrZero(message = "프레임 수는 0 이상이어야 합니다")
    @Max(value = 999999, message = "프레임 수는 999999 이하여야 합니다")
    private Integer frameCount;
    
    @Size(max = 500, message = "비디오 URL은 500자 이하여야 합니다")
    @Pattern(regexp = "^(https?://)?.*", message = "올바른 URL 형식이어야 합니다")
    private String videoUrl;
    
    @Size(max = 500, message = "썸네일 URL은 500자 이하여야 합니다")
    @Pattern(regexp = "^(https?://)?.*", message = "올바른 URL 형식이어야 합니다")
    private String thumbnailUrl;
}