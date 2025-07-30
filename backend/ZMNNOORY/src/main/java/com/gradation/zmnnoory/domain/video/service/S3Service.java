    package com.gradation.zmnnoory.domain.video.service;

    import com.gradation.zmnnoory.domain.video.dto.response.PreSignedUrlResponse;
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

        public PreSignedUrlResponse generatePreSignedUrl(
                Long videoId, Long userId, Long gameId,
                String originalFileName, String contentType) {

            try {
                String uuidFileName = generateUniqueFileName(originalFileName);
                String key = String.format("즈문누리/%d/%d/%s", userId, gameId, uuidFileName);

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

                return PreSignedUrlResponse.of(videoId, presignedRequest.url().toString(), key);

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
    }
