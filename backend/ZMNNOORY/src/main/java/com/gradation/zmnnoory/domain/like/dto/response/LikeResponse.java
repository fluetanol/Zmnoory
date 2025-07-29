package com.gradation.zmnnoory.domain.like.dto.response;

import com.gradation.zmnnoory.domain.like.entity.Like;
import lombok.Builder;
import lombok.Getter;

@Getter
public class LikeResponse {

    private final Long videoId;
    private final Long memberId;
    private final boolean isLiked;
    private final long likeCount;

    @Builder
    private LikeResponse(Long videoId, Long memberId, boolean isLiked, long likeCount) {
        this.videoId = videoId;
        this.memberId = memberId;
        this.isLiked = isLiked;
        this.likeCount = likeCount;
    }

    public static LikeResponse of(Like like, long likeCount) {
        return LikeResponse.builder()
                .videoId(like.getVideo().getId())
                .memberId(like.getMember().getId())
                .isLiked(true) // Like 엔티티가 존재한다는 건 좋아요 상태
                .likeCount(likeCount)
                .build();
    }

    public static LikeResponse notLiked(Long videoId, Long memberId, long likeCount) {
        return LikeResponse.builder()
                .videoId(videoId)
                .memberId(memberId)
                .isLiked(false)
                .likeCount(likeCount)
                .build();
    }
}
