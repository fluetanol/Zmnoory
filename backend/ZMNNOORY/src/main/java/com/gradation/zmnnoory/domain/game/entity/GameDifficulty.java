package com.gradation.zmnnoory.domain.game.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GameDifficulty {

    EASY("쉬움"),
    MEDIUM("중간"),
    HARD("어려움"),
    VERY_HARD("매우 어려움");

    private final String description;
}
