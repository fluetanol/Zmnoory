package com.gradation.zmnnoory.domain.participation.service;

import com.gradation.zmnnoory.domain.participation.dto.response.PublicUploadPresignedUrlResponse;
import com.gradation.zmnnoory.domain.video.exception.VideoUploadFailedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class S3ServiceTest {

    @Mock
    private S3Presigner presigner;

    @InjectMocks
    private S3Service s3Service;

    private final String TEST_BUCKET_NAME = "test-bucket";
    private final Long TEST_PARTICIPATION_ID = 1L;
    private final Long TEST_USER_ID = 2L;
    private final Long TEST_GAME_ID = 3L;

    @BeforeEach
    void setUp() {
        // Set the bucket name using reflection
        ReflectionTestUtils.setField(s3Service, "bucketName", TEST_BUCKET_NAME);
    }

    @Test
    @DisplayName("공개 업로드용 Presigned URL 생성 성공 테스트")
    void generatePublicUploadPresignedUrlsSuccess() {
        // given
        URL mockVideoUrl = mock(URL.class);
        URL mockThumbnailUrl = mock(URL.class);
        
        when(mockVideoUrl.toString()).thenReturn("https://test-bucket.s3.amazonaws.com/video-test-url");
        when(mockThumbnailUrl.toString()).thenReturn("https://test-bucket.s3.amazonaws.com/thumbnail-test-url");
        
        PresignedPutObjectRequest mockVideoPresignedRequest = mock(PresignedPutObjectRequest.class);
        PresignedPutObjectRequest mockThumbnailPresignedRequest = mock(PresignedPutObjectRequest.class);
        
        when(mockVideoPresignedRequest.url()).thenReturn(mockVideoUrl);
        when(mockThumbnailPresignedRequest.url()).thenReturn(mockThumbnailUrl);
        
        // First call returns video presigned request, second call returns thumbnail presigned request
        when(presigner.presignPutObject(any(software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest.class)))
            .thenReturn(mockVideoPresignedRequest)
            .thenReturn(mockThumbnailPresignedRequest);

        // when
        PublicUploadPresignedUrlResponse response = s3Service.generatePublicUploadPresignedUrls(
                TEST_PARTICIPATION_ID, TEST_USER_ID, TEST_GAME_ID);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getParticipationId()).isEqualTo(TEST_PARTICIPATION_ID);
        assertThat(response.getVideoUploadUrl()).isEqualTo(mockVideoUrl.toString());
        assertThat(response.getThumbnailUploadUrl()).isEqualTo(mockThumbnailUrl.toString());
        assertThat(response.getVideoObjectKey()).contains("zmnnoory/public/" + TEST_USER_ID + "/" + TEST_GAME_ID);
        assertThat(response.getThumbnailObjectKey()).contains("zmnnoory/public/" + TEST_USER_ID + "/" + TEST_GAME_ID);
        assertThat(response.getMessage()).contains("공개 영상 업로드용 Pre-signed URL이 생성되었습니다");
    }

    @Test
    @DisplayName("공개 업로드용 Presigned URL 생성 실패 테스트")
    void generatePublicUploadPresignedUrlsFailure() {
        // given
        when(presigner.presignPutObject(any(software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest.class)))
            .thenThrow(new RuntimeException("S3 error"));

        // when & then
        assertThrows(VideoUploadFailedException.class, () -> 
            s3Service.generatePublicUploadPresignedUrls(TEST_PARTICIPATION_ID, TEST_USER_ID, TEST_GAME_ID)
        );
    }
}