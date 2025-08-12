package com.gradation.zmnnoory.domain.member.entity;

import com.gradation.zmnnoory.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "referral_rewards")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReferralReward extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recommender_id", nullable = false)
    private Member recommender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recommended_id", nullable = false)
    private Member recommended;

    @Column(nullable = false)
    private Long recommenderReward;

    @Column(nullable = false)
    private Long recommendedReward;

    @Builder
    private ReferralReward(Member recommender, Member recommended, Long recommenderReward, Long recommendedReward) {
        this.recommender = recommender;
        this.recommended = recommended;
        this.recommenderReward = recommenderReward;
        this.recommendedReward = recommendedReward;
    }
}