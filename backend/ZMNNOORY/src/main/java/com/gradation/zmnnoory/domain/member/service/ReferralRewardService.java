package com.gradation.zmnnoory.domain.member.service;

import com.gradation.zmnnoory.domain.member.entity.Member;
import com.gradation.zmnnoory.domain.member.entity.ReferralReward;
import com.gradation.zmnnoory.domain.member.repository.ReferralRewardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReferralRewardService {

    private final ReferralRewardRepository referralRewardRepository;
    
    @Value("${app.referral.recommender-reward:1000}")
    private Long recommenderRewardAmount;
    
    @Value("${app.referral.recommended-reward:500}")
    private Long recommendedRewardAmount;

    @Transactional
    public void processReferralReward(Member recommender, Member recommended) {
        if (recommender == null) {
            log.info("추천인이 없어 리워드를 지급하지 않습니다. 회원: {}", recommended.getNickname());
            return;
        }

        // 추천인에게 포인트 지급
        recommender.addPoint(recommenderRewardAmount);
        
        // 추천받은 사람에게 포인트 지급
        recommended.addPoint(recommendedRewardAmount);
        
        // 리워드 이력 저장
        ReferralReward reward = ReferralReward.builder()
                .recommender(recommender)
                .recommended(recommended)
                .recommenderReward(recommenderRewardAmount)
                .recommendedReward(recommendedRewardAmount)
                .build();
        
        referralRewardRepository.save(reward);
        
        log.info("추천인 리워드 지급 완료 - 추천인: {} (+{}pt), 추천받은사람: {} (+{}pt)", 
                recommender.getNickname(), recommenderRewardAmount, 
                recommended.getNickname(), recommendedRewardAmount);
    }
}