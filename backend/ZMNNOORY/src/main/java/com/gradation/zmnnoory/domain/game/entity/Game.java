package com.gradation.zmnnoory.domain.game.entity;

import com.gradation.zmnnoory.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "games")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Game extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, length = 30)
    private String title;

    @Column(nullable = false, length = 255)
    private String description;

    @Column(nullable = false, length = 255)
    private String explanation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GameDifficulty difficulty;

    @Column(nullable = false)
    private Long point;

    @Column(length = 255)
    private String thumbnail;

    @Column
    private String requiredDataType;

    @Builder
    public Game(String title, String description, String explanation,
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

    public void update(GameUpdater updater) {
        this.title = updater.getTitle();
        this.description = updater.getDescription();
        this.explanation = updater.getExplanation();
        this.difficulty = updater.getDifficulty();
        this.point = updater.getPoint();
        this.thumbnail = updater.getThumbnail();
        this.requiredDataType = updater.getRequiredDataType();
    }
}
