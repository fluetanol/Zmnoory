package com.gradation.zmnnoory.domain.video.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
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

    public List<String> uploadBase64Images(List<String> base64Images) {
        return base64Images.stream()
                .map(this::uploadSingleImage)
                .toList();
    }

    private String uploadSingleImage(String base64String) {
        try {
            String[] parts = base64String.split(",");
            String metadata = parts[0]; // ex: data:image/png;base64
            String base64Data = parts[1];

            String contentType = metadata.split(":")[1].split(";")[0]; // image/png
            String extension = contentType.split("/")[1];              // png

            byte[] bytes = Base64.getDecoder().decode(base64Data);
            String key = "video/images/" + UUID.randomUUID() + "." + extension;

            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(key)
                            .contentType(contentType)
                            .build(),
                    RequestBody.fromBytes(bytes)
            );

            String uploadedUrl = s3Client.utilities().getUrl(
                    GetUrlRequest.builder()
                            .bucket(bucketName)
                            .key(key)
                            .build()).toExternalForm();

            log.info("이미지 업로드 성공: {}", uploadedUrl);
            return uploadedUrl;

        } catch (Exception e) {
            log.error("이미지 업로드 실패", e);
            throw new RuntimeException("이미지 업로드 실패", e);
        }
    }
}
