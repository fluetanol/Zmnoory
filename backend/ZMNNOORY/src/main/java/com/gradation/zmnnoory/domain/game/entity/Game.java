package com.gradation.zmnnoory.domain.game.entity;

import com.gradation.zmnnoory.common.entity.BaseEntity;
import com.gradation.zmnnoory.domain.participation.entity.Participation;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

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

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String thumbnail;

    @Column
    private String requiredDataType;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Participation> participations;

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
