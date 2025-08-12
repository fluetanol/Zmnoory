package com.gradation.zmnnoory.domain.participation.service;

import com.gradation.zmnnoory.domain.participation.dto.response.PresignedUrlResponse;
import com.gradation.zmnnoory.domain.participation.dto.response.PublicUploadPresignedUrlResponse;
import com.gradation.zmnnoory.domain.video.exception.VideoUploadFailedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    private final S3Presigner presigner;

    public PresignedUrlResponse generatePreSignedUrl(
            Long participationId, Long userId, Long gameId,
            String originalFileName, String contentType) {

        try {
            String uuidFileName = generateUniqueFileName(originalFileName);
            String key = String.format("zmnnoory/%d/%d/%s", userId, gameId, uuidFileName);

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(contentType)
                    .build();

            PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(15))
                    .putObjectRequest(putObjectRequest)
                    .build();

            PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(presignRequest);

            log.info("Pre-signed URL 생성 완료 - participationId: {}, key: {}, url: {}",
                    participationId, key, presignedRequest.url());

            return PresignedUrlResponse.of(
                    participationId,
                    presignedRequest.url().toString(),
                    key,
                    "Pre-signed URL이 생성되었습니다. 15분 내에 업로드를 완료해주세요."
            );


        } catch (Exception e) {
            log.error("Pre-signed URL 생성 실패: {}", e.getMessage(), e);
            throw new VideoUploadFailedException();
        }
    }

    private String generateUniqueFileName(String originalFileName) {
        String extension = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }
        return UUID.randomUUID().toString() + extension;
    }
    
    public PublicUploadPresignedUrlResponse generatePublicUploadPresignedUrls(
            Long participationId, Long userId, Long gameId) {

        try {
            // Generate unique filenames for video and thumbnail
            String videoUuidFileName = UUID.randomUUID().toString() + ".mp4";
            String thumbnailUuidFileName = UUID.randomUUID().toString() + ".jpg";
            
            // Create keys for video and thumbnail
            String videoKey = String.format("zmnnoory/public/%d/%d/%s", userId, gameId, videoUuidFileName);
            String thumbnailKey = String.format("zmnnoory/public/%d/%d/%s", userId, gameId, thumbnailUuidFileName);
            
            // Generate presigned URL for video
            PutObjectRequest videoRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(videoKey)
                    .contentType("video/mp4")
                    .build();

            PutObjectPresignRequest videoPresignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(15))
                    .putObjectRequest(videoRequest)
                    .build();

            PresignedPutObjectRequest presignedVideoRequest = presigner.presignPutObject(videoPresignRequest);
            
            // Generate presigned URL for thumbnail
            PutObjectRequest thumbnailRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(thumbnailKey)
                    .contentType("image/jpeg")
                    .build();

            PutObjectPresignRequest thumbnailPresignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(15))
                    .putObjectRequest(thumbnailRequest)
                    .build();

            PresignedPutObjectRequest presignedThumbnailRequest = presigner.presignPutObject(thumbnailPresignRequest);
            
            log.info("Public upload pre-signed URLs 생성 완료 - participationId: {}, videoKey: {}, thumbnailKey: {}",
                    participationId, videoKey, thumbnailKey);

            return PublicUploadPresignedUrlResponse.builder()
                    .participationId(participationId)
                    .videoUploadUrl(presignedVideoRequest.url().toString())
                    .videoObjectKey(videoKey)
                    .thumbnailUploadUrl(presignedThumbnailRequest.url().toString())
                    .thumbnailObjectKey(thumbnailKey)
                    .message("공개 영상 업로드용 Pre-signed URL이 생성되었습니다. 15분 내에 업로드를 완료해주세요.")
                    .build();

        } catch (Exception e) {
            log.error("Public upload pre-signed URLs 생성 실패: {}", e.getMessage(), e);
            throw new VideoUploadFailedException();
        }
    }
}
