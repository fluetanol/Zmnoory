package com.gradation.zmnnoory.domain.like.dto.response;

public record LikeResponse(
        Long videoId,
        Long memberId,
        boolean isLiked,
        long likeCount
) {
    public static LikeResponse of(Long videoId, Long memberId, boolean isLiked, long likeCount) {
        return new LikeResponse(videoId, memberId, isLiked, likeCount);
    }
}