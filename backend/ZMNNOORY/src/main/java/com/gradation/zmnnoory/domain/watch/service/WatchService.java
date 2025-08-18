package com.gradation.zmnnoory.domain.watch.service;

import com.gradation.zmnnoory.domain.member.entity.Member;
import com.gradation.zmnnoory.domain.member.exception.MemberNotFoundException;
import com.gradation.zmnnoory.domain.member.repository.MemberRepository;
import com.gradation.zmnnoory.domain.video.entity.Video;
import com.gradation.zmnnoory.domain.video.exception.VideoNotFoundException;
import com.gradation.zmnnoory.domain.video.repository.VideoRepository;
import com.gradation.zmnnoory.domain.watch.dto.response.WatchResponse;
import com.gradation.zmnnoory.domain.watch.entity.Watch;
import com.gradation.zmnnoory.domain.watch.repository.WatchRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class WatchService {

    private final WatchRepository watchRepository;
    private final MemberRepository memberRepository;
    private final VideoRepository videoRepository;

    // 1. 시청 기록 저장 (중복 방지 및 성능 최적화)
    public void recordWatch(Long memberId, Long videoId) {
        if (watchRepository.existsByMemberIdAndVideoId(memberId, videoId)) {
            log.info("이미 시청한 비디오입니다. memberId={}, videoId={}", memberId, videoId);
            return;
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("회원을 찾을 수 없습니다."));
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new VideoNotFoundException(videoId));

        Watch watch = Watch.builder()
                .member(member)
                .video(video)
                .build();

        watchRepository.save(watch);
        log.info("시청 기록 저장 완료. memberId={}, videoId={}", memberId, videoId);
    }

    // 2. 시청 여부 및 시청자 수 (성능 최적화)
    public WatchResponse getWatchStatus(Long memberId, Long videoId) {
        Optional<Watch> watchOptional = watchRepository.findByMemberIdAndVideoId(memberId, videoId);
        long watchCount = watchRepository.countByVideoId(videoId);

        return watchOptional
                .map(watch -> WatchResponse.of(watch, watchCount))
                .orElse(WatchResponse.notWatched(memberId, videoId, watchCount));
    }
}
