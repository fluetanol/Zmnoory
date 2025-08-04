package com.gradation.zmnnoory.domain.like.service;

import com.gradation.zmnnoory.domain.like.dto.response.LikeResponse;
import com.gradation.zmnnoory.domain.like.entity.Like;
import com.gradation.zmnnoory.domain.like.repository.LikeRepository;
import com.gradation.zmnnoory.domain.member.entity.Member;
import com.gradation.zmnnoory.domain.member.exception.MemberNotFoundException;
import com.gradation.zmnnoory.domain.member.repository.MemberRepository;
import com.gradation.zmnnoory.domain.video.entity.Video;
import com.gradation.zmnnoory.domain.video.exception.VideoNotFoundException;
import com.gradation.zmnnoory.domain.video.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class LikeService {

    private final LikeRepository likeRepository;
    private final VideoRepository videoRepository;
    private final MemberRepository memberRepository;

    public LikeResponse toggleLike(Long videoId, Long memberId) {
        // 성능 최적화: 한 번의 쿼리로 좋아요 상태 확인
        Optional<Like> existingLike = likeRepository.findByVideoIdAndMemberId(videoId, memberId);
        
        if (existingLike.isPresent()) {
            // 좋아요 취소
            likeRepository.delete(existingLike.get());
            log.info("좋아요 취소 완료. Video ID: {}, Member ID: {}", videoId, memberId);
            
            long likeCount = likeRepository.countByVideoId(videoId);
            return LikeResponse.notLiked(videoId, memberId, likeCount);
        } else {
            // 좋아요 추가 - 필요한 경우에만 엔티티 조회
            Video video = videoRepository.findById(videoId)
                    .orElseThrow(() -> new VideoNotFoundException(videoId));
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new MemberNotFoundException("맴버를 찾을 수 없습니다."));
            
            Like like = Like.builder()
                    .video(video)
                    .member(member)
                    .build();
            
            likeRepository.save(like);
            log.info("좋아요 추가 완료. Video ID: {}, Member ID: {}", videoId, memberId);
            
            long likeCount = likeRepository.countByVideoId(videoId);
            return LikeResponse.of(like, likeCount);
        }
    }

    @Transactional(readOnly = true)
    public LikeResponse getLikeStatus(Long videoId, Long memberId) {
        // 성능 최적화: 한 번의 쿼리로 좋아요 상태 확인
        Optional<Like> likeOptional = likeRepository.findByVideoIdAndMemberId(videoId, memberId);
        long likeCount = likeRepository.countByVideoId(videoId);
        
        return likeOptional
                .map(like -> LikeResponse.of(like, likeCount))
                .orElse(LikeResponse.notLiked(videoId, memberId, likeCount));
    }

    @Transactional(readOnly = true)
    public long getLikeCount(Long videoId) {
        // 성능 최적화: 불필요한 존재 여부 검증 제거
        // 존재하지 않는 videoId의 경우 count는 0을 반환
        return likeRepository.countByVideoId(videoId);
    }
}
