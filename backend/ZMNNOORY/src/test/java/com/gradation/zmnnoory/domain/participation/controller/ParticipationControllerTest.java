package com.gradation.zmnnoory.domain.participation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gradation.zmnnoory.common.dto.BaseResponse;
import com.gradation.zmnnoory.domain.participation.dto.request.PublicUploadPresignedUrlRequest;
import com.gradation.zmnnoory.domain.participation.dto.response.PublicUploadPresignedUrlResponse;
import com.gradation.zmnnoory.domain.participation.exception.ParticipationAlreadyCompletedException;
import com.gradation.zmnnoory.domain.participation.exception.ParticipationNotFoundException;
import com.gradation.zmnnoory.domain.participation.service.ParticipationService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import com.gradation.zmnnoory.domain.member.entity.Role;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
class ParticipationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ParticipationService participationService;

    private final String BASE_URL = "/api/participations";
    private final String TEST_EMAIL = "test@test.com";
    private PublicUploadPresignedUrlRequest validRequest;
    private PublicUploadPresignedUrlRequest invalidRequest;
    private PublicUploadPresignedUrlResponse testResponse;

    private void setAuthentication(String email, Role role) {
        UserDetails userDetails = new User(
                email,
                "password",
                Collections.singletonList(new SimpleGrantedAuthority(role.getAuthority()))
        );
        
        UsernamePasswordAuthenticationToken authentication = 
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private void clearAuthentication() {
        SecurityContextHolder.clearContext();
    }

    @BeforeEach
    void setUp() {
        // Set up authentication for each test
        setAuthentication(TEST_EMAIL, Role.USER);
        
        // 유효한 요청 설정
        validRequest = new PublicUploadPresignedUrlRequest("test@test.com", "테스트 게임");

        // 유효하지 않은 요청 설정 (이메일 누락)
        invalidRequest = new PublicUploadPresignedUrlRequest("", "테스트 게임");
        
        // 테스트 응답 설정
        testResponse = PublicUploadPresignedUrlResponse.builder()
                .participationId(1L)
                .videoUploadUrl("https://test-bucket.s3.amazonaws.com/video-test-url")
                .videoObjectKey("zmnnoory/public/1/1/test-video.mp4")
                .thumbnailUploadUrl("https://test-bucket.s3.amazonaws.com/thumbnail-test-url")
                .thumbnailObjectKey("zmnnoory/public/1/1/test-thumbnail.jpg")
                .message("공개 영상 업로드용 Pre-signed URL이 생성되었습니다. 15분 내에 업로드를 완료해주세요.")
                .build();
    }
    
    @AfterEach
    void tearDown() {
        // Clear authentication after each test
        clearAuthentication();
    }

    @Test
    @DisplayName("공개 업로드용 Presigned URL 요청 성공 테스트")
    void getPublicUploadPresignedUrlSuccess() throws Exception {
        // given
        given(participationService.getPublicUploadPresignedUrl(any(PublicUploadPresignedUrlRequest.class)))
                .willReturn(testResponse);

        // when
        ResultActions result = mockMvc.perform(
                post(BASE_URL + "/public-presigned-url")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest))
        );

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.body.data.participationId").value(1))
                .andExpect(jsonPath("$.body.data.videoUploadUrl").value("https://test-bucket.s3.amazonaws.com/video-test-url"))
                .andExpect(jsonPath("$.body.data.thumbnailUploadUrl").value("https://test-bucket.s3.amazonaws.com/thumbnail-test-url"));
    }

    @Test
    @DisplayName("유효하지 않은 요청으로 Presigned URL 요청 실패 테스트")
    void getPublicUploadPresignedUrlInvalidRequest() throws Exception {
        // when
        ResultActions result = mockMvc.perform(
                post(BASE_URL + "/public-presigned-url")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest))
        );

        // then
        result.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("존재하지 않는 참여에 대한 Presigned URL 요청 실패 테스트")
    void getPublicUploadPresignedUrlParticipationNotFound() throws Exception {
        // given
        when(participationService.getPublicUploadPresignedUrl(any(PublicUploadPresignedUrlRequest.class)))
                .thenThrow(new ParticipationNotFoundException());

        // when
        ResultActions result = mockMvc.perform(
                post(BASE_URL + "/public-presigned-url")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest))
        );

        // then
        result.andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("이미 완료된 참여에 대한 Presigned URL 요청 실패 테스트")
    void getPublicUploadPresignedUrlParticipationAlreadyCompleted() throws Exception {
        // given
        when(participationService.getPublicUploadPresignedUrl(any(PublicUploadPresignedUrlRequest.class)))
                .thenThrow(new ParticipationAlreadyCompletedException());

        // when
        ResultActions result = mockMvc.perform(
                post(BASE_URL + "/public-presigned-url")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest))
        );

        // then
        result.andExpect(status().isConflict());
    }
}