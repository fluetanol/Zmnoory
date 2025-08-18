package com.gradation.zmnnoory.domain.video.service;

import com.gradation.zmnnoory.domain.video.dto.request.Base64ImageRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoImageUploadService {

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    private final S3Client s3Client;

    public void uploadBase64Images(Long userId, Long gameId, List<Base64ImageRequest> images) {
        images.forEach(image -> uploadSingleImage(userId, gameId, image));
    }

    private void uploadSingleImage(Long userId, Long gameId, Base64ImageRequest image) {
        try {
            String fileName = image.fileName();
            String base64String = image.data();

            String[] parts = base64String.split(",");
            String metadata = parts[0];
            String base64Data = parts[1];

            String contentType = metadata.split(":")[1].split(";")[0];

            byte[] bytes = Base64.getDecoder().decode(base64Data);
            String extension = fileName.substring(fileName.lastIndexOf("."));
            String nameWithoutExtension = fileName.substring(0, fileName.lastIndexOf("."));
            String newFileName = nameWithoutExtension + "_" + UUID.randomUUID() + extension;

            String key = String.format("zmnnoory/%d/%d/%s", userId, gameId, newFileName);

            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(key)
                            .contentType(contentType)
                            .build(),
                    RequestBody.fromBytes(bytes)
            );

            log.info("이미지 업로드 성공 - key: {}", key);

        } catch (Exception e) {
            log.error("이미지 업로드 실패 - {}", image.fileName(), e);
            throw new RuntimeException("이미지 업로드 실패", e);
        }
    }
}