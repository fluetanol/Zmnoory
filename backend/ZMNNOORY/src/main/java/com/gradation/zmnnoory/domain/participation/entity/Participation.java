package com.gradation.zmnnoory.domain.participation.entity;

import com.gradation.zmnnoory.common.entity.BaseEntity;
import com.gradation.zmnnoory.domain.game.entity.Game;
import com.gradation.zmnnoory.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "participations")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Participation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ParticipationStatus status;

    public void complete() {
        if (this.status != ParticipationStatus.NOT_PARTICIPATED) {
            throw new IllegalStateException("이미 완료된 게임 참여입니다. 리워드를 다시 지급할 수 없습니다.");
        }
        this.status = ParticipationStatus.COMPLETED;
    }

    @Builder
    public Participation(Member member, Game game, ParticipationStatus status) {
        this.member = member;
        this.game = game;
        this.status = status;
    }
}
