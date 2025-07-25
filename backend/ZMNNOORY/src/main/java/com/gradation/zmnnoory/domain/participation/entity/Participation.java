package com.gradation.zmnnoory.domain.participation.entity;

import com.gradation.zmnnoory.common.entity.BaseEntity;
import com.gradation.zmnnoory.domain.member.entity.Member;
import com.gradation.zmnnoory.domain.participation.status.ParticipationStatus;
import com.gradation.zmnnoory.domain.game.entity.Game;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@Table(name = "participations")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Participation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    private LocalDate startedAt;
    private LocalDate endedAt;

    @Enumerated(EnumType.STRING)
    private ParticipationStatus status;

    private Integer frameCount;
    private String videoUrl;
    private String thumbnailUrl;

    public void updateMediaInfo(Integer frameCount, String videoUrl, String thumbnailUrl) {
        if (frameCount != null) this.frameCount = frameCount;
        if (videoUrl != null) this.videoUrl = videoUrl;
        if (thumbnailUrl != null) this.thumbnailUrl = thumbnailUrl;
    }

    public void complete() {
        if (this.status != ParticipationStatus.IN_PROGRESS) {
            throw new IllegalStateException("진행 중인 participation이 아닙니다.");
        }
        this.status = ParticipationStatus.COMPLETED;
        this.endedAt = LocalDate.now();
    }

    @Builder
    public Participation(Member member, Game game, LocalDate startedAt,
                         LocalDate endedAt, ParticipationStatus status,
                         Integer frameCount, String videoUrl, String thumbnailUrl) {
        this.member = member;
        this.game = game;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
        this.status = status;
        this.frameCount = frameCount;
        this.videoUrl = videoUrl;
        this.thumbnailUrl = thumbnailUrl;
    }
}
