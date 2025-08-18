package com.gradation.zmnnoory.domain.member.entity;

import com.gradation.zmnnoory.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(
        name = "referral_rewards",
        uniqueConstraints = {
                // 한 추천인당 1건만 생성 가능
                @UniqueConstraint(name = "uk_referral_once_per_recommender", columnNames = "recommender_id")
        },
        indexes = {
                @Index(name = "ix_referral_recommended", columnList = "recommended_id")
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReferralReward extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recommender_id", nullable = false)
    private Member recommender;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recommended_id", nullable = false)
    private Member recommended;

    @Column(nullable = false)
    private Long recommenderReward;

    @Column(nullable = false)
    private Long recommendedReward;

    @Builder
    private ReferralReward(Member recommender, Member recommended,
                           Long recommenderReward, Long recommendedReward) {
        if (recommender == null || recommended == null) throw new IllegalArgumentException("members required");
        if (recommender.equals(recommended)) throw new IllegalArgumentException("self referral not allowed");
        if (recommenderReward == null || recommenderReward < 0) throw new IllegalArgumentException("invalid recommenderReward");
        if (recommendedReward == null || recommendedReward < 0) throw new IllegalArgumentException("invalid recommendedReward");
        this.recommender = recommender;
        this.recommended = recommended;
        this.recommenderReward = recommenderReward;
        this.recommendedReward = recommendedReward;
    }
}

