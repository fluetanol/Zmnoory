package com.gradation.zmnnoory.domain.participation.service;

import com.gradation.zmnnoory.domain.game.entity.Game;
import com.gradation.zmnnoory.domain.game.exception.GameNotFoundException;
import com.gradation.zmnnoory.domain.game.repository.GameRepository;
import com.gradation.zmnnoory.domain.member.entity.Member;
import com.gradation.zmnnoory.domain.member.exception.MemberNotFoundException;
import com.gradation.zmnnoory.domain.member.repository.MemberRepository;
import com.gradation.zmnnoory.domain.participation.dto.request.CompleteParticipationRequest;
import com.gradation.zmnnoory.domain.participation.dto.request.PresignedUrlRequest;
import com.gradation.zmnnoory.domain.participation.dto.request.PublicUploadPresignedUrlRequest;
import com.gradation.zmnnoory.domain.participation.dto.request.StartParticipationRequest;
import com.gradation.zmnnoory.domain.participation.dto.response.ParticipationEndResponse;
import com.gradation.zmnnoory.domain.participation.dto.response.ParticipationResponse;
import com.gradation.zmnnoory.domain.participation.dto.response.PresignedUrlResponse;
import com.gradation.zmnnoory.domain.participation.dto.response.PublicUploadPresignedUrlResponse;
import com.gradation.zmnnoory.domain.participation.entity.Participation;
import com.gradation.zmnnoory.domain.participation.entity.ParticipationStatus;
import com.gradation.zmnnoory.domain.participation.exception.AlreadyParticipatedException;
import com.gradation.zmnnoory.domain.participation.exception.ParticipationAlreadyCompletedException;
import com.gradation.zmnnoory.domain.participation.exception.ParticipationNotFoundException;
import com.gradation.zmnnoory.domain.participation.repository.ParticipationRepository;
import com.gradation.zmnnoory.domain.video.dto.response.VideoResponse;
import com.gradation.zmnnoory.domain.video.service.VideoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ParticipationService {

    private final ParticipationRepository participationRepository;
    private final MemberRepository memberRepository;
    private final GameRepository gameRepository;
    private final VideoService videoService;
    private final S3Service s3Service;

    // 1. 게임 참여 시작
    public ParticipationResponse startParticipation(StartParticipationRequest request) {
        Member member = memberRepository.findByEmail(request.email())
                .orElseThrow(MemberNotFoundException::new);

        Game game = gameRepository.findByTitle(request.gameTitle())
                .orElseThrow(() -> new GameNotFoundException(request.gameTitle()));

        // 중복 참여 검증
        if (participationRepository.existsByMemberEmailAndGameTitle(request.email(), request.gameTitle())) {
            throw new AlreadyParticipatedException();
        }

        Participation participation = Participation.builder()
                .member(member)
                .game(game)
                .status(ParticipationStatus.NOT_PARTICIPATED)
                .build();

        return ParticipationResponse.of(participationRepository.save(participation));
    }

    // 2. Presigned URL 생성
    public PresignedUrlResponse getPresignedUrl(PresignedUrlRequest request) {
        Participation participation = participationRepository
                .findByMemberEmailAndGameTitle(request.email(), request.gameTitle())
                .orElseThrow(ParticipationNotFoundException::new);

        if (participation.getStatus() == ParticipationStatus.COMPLETED) {
            throw new ParticipationAlreadyCompletedException();
        }

        return s3Service.generatePreSignedUrl(
                participation.getId(),
                participation.getMember().getId(),
                participation.getGame().getId(),
                request.fileName(),
                request.contentType()
        );
    }

    // 3. 참여 완료 처리 (업로드 성공 후)
    public ParticipationEndResponse completeParticipation(CompleteParticipationRequest request) {
        Participation participation = participationRepository
                .findByMemberEmailAndGameTitle(request.email(), request.gameTitle())
                .orElseThrow(ParticipationNotFoundException::new);

        // 이미 완료된 참여 체크
        if (participation.getStatus() == ParticipationStatus.COMPLETED) {
            throw new ParticipationAlreadyCompletedException();
        }

        // 참여 완료 처리
        participation.complete();

        // 비디오 엔티티 생성 (실제 URL과 함께)
        VideoResponse video = videoService.createVideoWithUploadedData(
                participation,
                request.videoUrl(),
                request.thumbnailUrl(),
                request.title(),
                request.description(),
                request.isPublic()
        );

        // 리워드 포인트 지급
        Long rewardPoint = participation.getGame().getPoint();
        Member member = participation.getMember();
        member.addPoint(rewardPoint);

        return ParticipationEndResponse.of(participation, video.id());
    }


    // 4. 한 멤버의 전체 참여 리스트
    public List<ParticipationResponse> getParticipationsByMember(Long memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new MemberNotFoundException();
        }
        return participationRepository.findByMemberId(memberId).stream()
                .map(ParticipationResponse::of)
                .toList();
    }

    public String deleteParticipation(Member member) {
        participationRepository.findAllByMember(member).forEach(videoService::deleteVideo);
        participationRepository.deleteAllByMember(member);
        return "삭제 완료";
    }

    public PublicUploadPresignedUrlResponse getPublicUploadPresignedUrl(PublicUploadPresignedUrlRequest request) {
        Participation participation = participationRepository
                .findByMemberEmailAndGameTitle(request.email(), request.gameTitle())
                .orElseThrow(ParticipationNotFoundException::new);

        if (participation.getStatus() == ParticipationStatus.COMPLETED) {
            throw new ParticipationAlreadyCompletedException();
        }

        return s3Service.generatePublicUploadPresignedUrls(
                participation.getId(),
                participation.getMember().getId(),
                participation.getGame().getId()
        );
    }
}
