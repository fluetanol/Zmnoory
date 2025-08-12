package com.gradation.zmnnoory.domain.member.service;

import com.gradation.zmnnoory.domain.participation.dto.response.PresignedUrlResponse;
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
public class MemberProfileImageService {


    @Value("${aws.s3.bucket-upload-name}")
    private String bucketUploadName;

    @Value("${aws.s3.region}")
    private String region;

    private final S3Presigner presigner;

    public PresignedUrlResponse generatePreSignedUrlForProfile(
            String originalFileName, String contentType) {

        try {
            String uuidFileName = generateUniqueFileName(originalFileName);
            String key = String.format("profile/%s", uuidFileName);

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketUploadName)
                    .key(key)
                    .contentType(contentType)
                    .build();

            PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(15))
                    .putObjectRequest(putObjectRequest)
                    .build();

            PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(presignRequest);

            log.info("프로필 이미지 Pre-signed URL 생성 완료 - key: {}, url: {}",
                    key, presignedRequest.url());

            String publicUrl = String.format("https://%s.s3.%s.amazonaws.com/%s", bucketUploadName, region, key);

            return PresignedUrlResponse.of(
                    null,
                    presignedRequest.url().toString(),
                    key,
                    "프로필 이미지 Pre-signed URL이 생성되었습니다. 15분 내에 업로드를 완료해주세요."
            );

        } catch (Exception e) {
            log.error("프로필 이미지 Pre-signed URL 생성 실패: {}", e.getMessage(), e);
            throw new VideoUploadFailedException();
        }
    }

    public String getPublicUrl(String key) {
        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketUploadName, region, key);
    }

    private String generateUniqueFileName(String originalFileName) {
        String extension = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }
        return UUID.randomUUID().toString() + extension;
    }
}