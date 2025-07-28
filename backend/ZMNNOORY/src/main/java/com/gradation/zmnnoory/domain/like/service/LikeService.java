package com.gradation.zmnnoory.domain.like.service;

import com.gradation.zmnnoory.domain.like.dto.response.LikeResponse;
import com.gradation.zmnnoory.domain.like.entity.Like;
import com.gradation.zmnnoory.domain.like.exception.LikeAlreadyExistsException;
import com.gradation.zmnnoory.domain.like.exception.LikeNotFoundException;
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

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class LikeService {

    private final LikeRepository likeRepository;
    private final VideoRepository videoRepository;
    private final MemberRepository memberRepository;

    public LikeResponse toggleLike(Long videoId, Long memberId) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new VideoNotFoundException(videoId));
        
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("맴버를 찾을 수 없습니다."));

        boolean isLiked = likeRepository.existsByVideoIdAndMemberId(videoId, memberId);
        
        if (isLiked) {
            Like like = likeRepository.findByVideoIdAndMemberId(videoId, memberId)
                    .orElseThrow(() -> new LikeNotFoundException(videoId, memberId));
            
            likeRepository.delete(like);
            log.info("좋아요 취소 완료. Video ID: {}, Member ID: {}", videoId, memberId);
            isLiked = false;
        } else {
            Like like = Like.builder()
                    .video(video)
                    .member(member)
                    .build();
            
            likeRepository.save(like);
            log.info("좋아요 추가 완료. Video ID: {}, Member ID: {}", videoId, memberId);
            isLiked = true;
        }

        long likeCount = likeRepository.countByVideoId(videoId);
        return LikeResponse.of(videoId, memberId, isLiked, likeCount);
    }

    @Transactional(readOnly = true)
    public LikeResponse getLikeStatus(Long videoId, Long memberId) {
        if (!videoRepository.existsById(videoId)) {
            throw new VideoNotFoundException(videoId);
        }
        
        if (!memberRepository.existsById(memberId)) {
            throw new MemberNotFoundException("맴버를 찾을 수 없습니다.");
        }

        boolean isLiked = likeRepository.existsByVideoIdAndMemberId(videoId, memberId);
        long likeCount = likeRepository.countByVideoId(videoId);
        
        return LikeResponse.of(videoId, memberId, isLiked, likeCount);
    }

    @Transactional(readOnly = true)
    public long getLikeCount(Long videoId) {
        if (!videoRepository.existsById(videoId)) {
            throw new VideoNotFoundException(videoId);
        }

        return likeRepository.countByVideoId(videoId);
    }
}
