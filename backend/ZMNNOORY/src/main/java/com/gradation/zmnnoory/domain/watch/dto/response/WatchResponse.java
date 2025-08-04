package com.gradation.zmnnoory.domain.watch.dto.response;

import com.gradation.zmnnoory.domain.watch.entity.Watch;
import lombok.Builder;
import lombok.Getter;

@Getter
public class WatchResponse {

    private final Long memberId;
    private final Long videoId;
    private final boolean watched;
    private final long watchCount;

    @Builder
    private WatchResponse(Long memberId, Long videoId, boolean watched, long watchCount) {
        this.memberId = memberId;
        this.videoId = videoId;
        this.watched = watched;
        this.watchCount = watchCount;
    }

    public static WatchResponse of(Watch watch, long watchCount) {
        return WatchResponse.builder()
                .memberId(watch.getMember().getId())
                .videoId(watch.getVideo().getId())
                .watched(true)
                .watchCount(watchCount)
                .build();
    }

    public static WatchResponse notWatched(Long memberId, Long videoId, long watchCount) {
        return WatchResponse.builder()
                .memberId(memberId)
                .videoId(videoId)
                .watched(false)
                .watchCount(watchCount)
                .build();
    }
}
