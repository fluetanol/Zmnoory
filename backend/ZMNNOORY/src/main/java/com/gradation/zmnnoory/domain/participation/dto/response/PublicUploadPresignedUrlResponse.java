package com.gradation.zmnnoory.domain.participation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
public class PublicUploadPresignedUrlResponse {

    @Schema(description = "참여 ID", example = "1")
    private final Long participationId;

    @Schema(description = "S3 비디오 업로드용 Pre-signed URL", example = "https://s3.bucket.amazonaws.com/...")
    private final String videoUploadUrl;

    @Schema(description = "S3 비디오 저장 경로 (Object Key)", example = "즈문누리/1/1/uuid.mp4")
    private final String videoObjectKey;

    @Schema(description = "S3 섬네일 업로드용 Pre-signed URL", example = "https://s3.bucket.amazonaws.com/...")
    private final String thumbnailUploadUrl;

    @Schema(description = "S3 섬네일 저장 경로 (Object Key)", example = "즈문누리/1/1/uuid.mp4")
    private final String thumbnailObjectKey;

    @Schema(description = "안내 메시지", example = "Pre-signed URL이 생성되었습니다. 15분 내에 업로드를 완료해주세요.")
    private final String message;

    @Builder
    public PublicUploadPresignedUrlResponse(Long participationId,
                                            String videoUploadUrl,
                                            String videoObjectKey,
                                            String thumbnailUploadUrl,
                                            String thumbnailObjectKey,
                                            String message) {
        this.participationId = participationId;
        this.videoUploadUrl = videoUploadUrl;
        this.videoObjectKey = videoObjectKey;
        this.thumbnailUploadUrl = thumbnailUploadUrl;
        this.thumbnailObjectKey = thumbnailObjectKey;
        this.message = message;
    }
}
