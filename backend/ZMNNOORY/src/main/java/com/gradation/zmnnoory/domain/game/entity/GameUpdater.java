package com.gradation.zmnnoory.domain.game.entity;

import lombok.Getter;

@Getter
public class GameUpdater {

    private final String title;
    private final String description;
    private final String explanation;
    private final GameDifficulty difficulty;
    private final Long point;
    private final String thumbnail;
    private final String requiredDataType;

    public GameUpdater(String title, String description, String explanation,
                       GameDifficulty difficulty, Long point,
                       String thumbnail, String requiredDataType) {
        this.title = title;
        this.description = description;
        this.explanation = explanation;
        this.difficulty = difficulty;
        this.point = point;
        this.thumbnail = thumbnail;
        this.requiredDataType = requiredDataType;
    }
}
