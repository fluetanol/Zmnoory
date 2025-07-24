package com.gradation.zmnnoory.domain.game.dto;

import com.gradation.zmnnoory.domain.game.entity.Game;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
public class GameResponse {

    @Schema(description = "게임 ID", example = "1")
    private final Long id;

    @Schema(description = "게임 제목", example = "도형 맞추기")
    private final String title;

    @Schema(description = "간단 설명", example = "기하학 도형을 맞추는 게임입니다")
    private final String description;

    @Schema(description = "상세 설명", example = "회전 가능한 도형을 정해진 위치에 맞추는 게임입니다")
    private final String explanation;

    @Schema(description = "게임 난이도 (한글 설명)", example = "중간")
    private final String difficulty;

    @Schema(description = "보상 포인트", example = "500")
    private final Long point;

    @Schema(description = "썸네일 이미지 URL", example = "https://example.com/image.png")
    private final String thumbnail;

    @Schema(description = "요구 데이터 유형", example = "얼굴 데이터")
    private final String requireDataType;

    @Builder
    private GameResponse(Long id, String title, String description, String explanation,
                         String difficulty, Long point, String thumbnail, String requireDataType) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.explanation = explanation;
        this.difficulty = difficulty;
        this.point = point;
        this.thumbnail = thumbnail;
        this.requireDataType = requireDataType;
    }

    public static GameResponse of(Game game) {
        return GameResponse.builder()
                .id(game.getId())
                .title(game.getTitle())
                .description(game.getDescription())
                .explanation(game.getExplanation())
                .difficulty(game.getDifficulty().getDescription())
                .point(game.getPoint())
                .thumbnail(game.getThumbnail())
                .requireDataType(game.getRequiredDataType())
                .build();
    }
}