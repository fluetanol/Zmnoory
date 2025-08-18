package com.gradation.zmnnoory.domain.participation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
public class PresignedUrlResponse {

    @Schema(description = "참여 ID", example = "1")
    private final Long participationId;

    @Schema(description = "S3 업로드용 Pre-signed URL", example = "https://s3.bucket.amazonaws.com/...")
    private final String uploadUrl;

    @Schema(description = "S3 저장 경로 (Object Key)", example = "즈문누리/1/1/uuid.mp4")
    private final String objectKey;

    @Schema(description = "안내 메시지", example = "Pre-signed URL이 생성되었습니다. 15분 내에 업로드를 완료해주세요.")
    private final String message;

    @Builder
    private PresignedUrlResponse(Long participationId, String uploadUrl, String objectKey, String message) {
        this.participationId = participationId;
        this.uploadUrl = uploadUrl;
        this.objectKey = objectKey;
        this.message = message;
    }

    public static PresignedUrlResponse of(Long participationId, String uploadUrl, String objectKey, String message) {
        return PresignedUrlResponse.builder()
                .participationId(participationId)
                .uploadUrl(uploadUrl)
                .objectKey(objectKey)
                .message(message)
                .build();
    }
}
