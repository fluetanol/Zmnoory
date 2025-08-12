package com.gradation.zmnnoory.domain.participation.service;

import com.gradation.zmnnoory.domain.game.entity.Game;
import com.gradation.zmnnoory.domain.game.repository.GameRepository;
import com.gradation.zmnnoory.domain.member.entity.Gender;
import com.gradation.zmnnoory.domain.member.entity.Member;
import com.gradation.zmnnoory.domain.member.entity.Role;
import com.gradation.zmnnoory.domain.member.repository.MemberRepository;
import com.gradation.zmnnoory.domain.participation.dto.request.PublicUploadPresignedUrlRequest;
import com.gradation.zmnnoory.domain.participation.dto.response.PublicUploadPresignedUrlResponse;
import com.gradation.zmnnoory.domain.participation.entity.Participation;
import com.gradation.zmnnoory.domain.participation.entity.ParticipationStatus;
import com.gradation.zmnnoory.domain.participation.exception.ParticipationAlreadyCompletedException;
import com.gradation.zmnnoory.domain.participation.exception.ParticipationNotFoundException;
import com.gradation.zmnnoory.domain.participation.repository.ParticipationRepository;
import com.gradation.zmnnoory.domain.video.service.VideoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ParticipationServiceTest {

    @Mock
    private ParticipationRepository participationRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private VideoService videoService;

    @Mock
    private S3Service s3Service;

    @InjectMocks
    private ParticipationService participationService;

    private Member testMember;
    private Game testGame;
    private Participation testParticipation;
    private PublicUploadPresignedUrlRequest testRequest;
    private PublicUploadPresignedUrlResponse testResponse;

    @BeforeEach
    void setUp() {
        // 테스트 멤버 설정
        testMember = Member.builder()
                .email("test@test.com")
                .password("password")
                .gender(Gender.MALE)
                .nickname("tester")
                .birthday(LocalDate.of(2000, 1, 1))
                .role(Role.USER)
                .build();

        // ID 필드 설정을 위한 리플렉션 사용
        try {
            java.lang.reflect.Field idField = testMember.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(testMember, 1L);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 테스트 게임 설정
        testGame = Game.builder()
                .title("테스트 게임")
                .description("테스트 게임 설명")
                .point(100L)
                .build();

        // ID 필드 설정을 위한 리플렉션 사용
        try {
            java.lang.reflect.Field idField = testGame.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(testGame, 1L);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 테스트 참여 설정
        testParticipation = Participation.builder()
                .member(testMember)
                .game(testGame)
                .status(ParticipationStatus.NOT_PARTICIPATED)
                .build();

        // ID 필드 설정을 위한 리플렉션 사용
        try {
            java.lang.reflect.Field idField = testParticipation.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(testParticipation, 1L);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 테스트 요청 설정
        testRequest = new PublicUploadPresignedUrlRequest("test@test.com", "테스트 게임");

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

    @Nested
    @DisplayName("공개 업로드용 Presigned URL 테스트")
    class PublicUploadPresignedUrlTest {

        @Test
        @DisplayName("공개 업로드용 Presigned URL 생성 성공 테스트")
        void getPublicUploadPresignedUrlSuccess() {
            // given
            when(participationRepository.findByMemberEmailAndGameTitle("test@test.com", "테스트 게임"))
                    .thenReturn(Optional.of(testParticipation));
            when(s3Service.generatePublicUploadPresignedUrls(anyLong(), anyLong(), anyLong()))
                    .thenReturn(testResponse);

            // when
            PublicUploadPresignedUrlResponse response = participationService.getPublicUploadPresignedUrl(testRequest);

            // then
            assertThat(response).isNotNull();
            assertThat(response.getParticipationId()).isEqualTo(1L);
            assertThat(response.getVideoUploadUrl()).isEqualTo("https://test-bucket.s3.amazonaws.com/video-test-url");
            assertThat(response.getThumbnailUploadUrl()).isEqualTo("https://test-bucket.s3.amazonaws.com/thumbnail-test-url");
        }

        @Test
        @DisplayName("존재하지 않는 참여에 대한 Presigned URL 요청 테스트")
        void getPublicUploadPresignedUrlParticipationNotFound() {
            // given
            when(participationRepository.findByMemberEmailAndGameTitle("test@test.com", "테스트 게임"))
                    .thenReturn(Optional.empty());

            // when & then
            assertThrows(ParticipationNotFoundException.class, () ->
                    participationService.getPublicUploadPresignedUrl(testRequest)
            );
        }

        @Test
        @DisplayName("이미 완료된 참여에 대한 Presigned URL 요청 테스트")
        void getPublicUploadPresignedUrlParticipationAlreadyCompleted() {
            // given
            testParticipation.complete(); // 참여 상태를 COMPLETED로 변경
            when(participationRepository.findByMemberEmailAndGameTitle("test@test.com", "테스트 게임"))
                    .thenReturn(Optional.of(testParticipation));

            // when & then
            assertThrows(ParticipationAlreadyCompletedException.class, () ->
                    participationService.getPublicUploadPresignedUrl(testRequest)
            );
        }
    }
}