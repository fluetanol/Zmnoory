package com.gradation.zmnnoory.domain.game.dto;

import com.gradation.zmnnoory.domain.game.entity.GameDifficulty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "게임 필터 검색 요청")
public record GameSearchRequest(

        @Schema(description = "게임 난이도", example = "MEDIUM")
        GameDifficulty difficulty

) {}
