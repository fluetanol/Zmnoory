package com.gradation.zmnnoory.domain.participation.dto.response;

public record PresignedUrlResponse(
        Long participationId,
        String uploadUrl,
        String objectKey,
        String message
) {
    public static PresignedUrlResponse of(
            Long participationId, String uploadUrl, String objectKey) {
        return new PresignedUrlResponse(
                participationId,
                uploadUrl,
                objectKey,
                "Pre-signed URL이 생성되었습니다. 15분 내에 업로드를 완료해주세요."
        );
    }
}