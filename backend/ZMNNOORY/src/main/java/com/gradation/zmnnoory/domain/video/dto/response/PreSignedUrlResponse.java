package com.gradation.zmnnoory.domain.video.dto.response;

public record PreSignedUrlResponse(
        Long videoId,
        String uploadUrl,
        String key,
        String message
) {

    public static PreSignedUrlResponse of(Long videoId, String uploadUrl, String key) {
        return new PreSignedUrlResponse(
                videoId,
                uploadUrl,
                key,
                "Pre-signed URL이 생성되었습니다. 15분 내에 업로드를 완료해주세요."
        );
    }
}
