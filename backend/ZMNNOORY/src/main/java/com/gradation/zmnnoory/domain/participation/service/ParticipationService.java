package com.gradation.zmnnoory.domain.participation.service;

import com.gradation.zmnnoory.domain.member.entity.Member;
import com.gradation.zmnnoory.domain.member.repository.MemberRepository;
import com.gradation.zmnnoory.domain.participation.dto.ParticipationResponse;
import com.gradation.zmnnoory.domain.participation.dto.UpdateParticipationRequest;
import com.gradation.zmnnoory.domain.participation.entity.Participation;
import com.gradation.zmnnoory.domain.participation.repository.ParticipationRepository;
import com.gradation.zmnnoory.domain.participation.status.ParticipationStatus;
import com.gradation.zmnnoory.domain.stage.entity.Stage;
import com.gradation.zmnnoory.domain.stage.repository.StageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ParticipationService {

    private final ParticipationRepository participationRepository;
    private final MemberRepository memberRepository;
    private final StageRepository stageRepository;

    public ParticipationResponse startParticipation(String email, String stageTitle) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        
        Stage stage = stageRepository.findByTitle(stageTitle)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 스테이지입니다."));

        Participation participation = Participation.builder()
                .member(member)
                .stage(stage)
                .startedAt(LocalDate.now())
                .status(ParticipationStatus.IN_PROGRESS)
                .build();

        Participation savedParticipation = participationRepository.save(participation);
        return ParticipationResponse.of(savedParticipation);
    }

    public ParticipationResponse endParticipation(UUID participationId) {
        Participation participation = findParticipationById(participationId);
        participation.complete();
        return ParticipationResponse.of(participation);
    }

    public boolean isFirstParticipation(Long memberId, Long stageId) {
        return !participationRepository.existsByMemberIdAndStageId(memberId, stageId);
    }

    public ParticipationResponse getParticipation(UUID participationId) {
        Participation participation = findParticipationById(participationId);
        return ParticipationResponse.of(participation);
    }

    private Participation findParticipationById(UUID participationId) {
        return participationRepository.findById(participationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 participation입니다."));
    }

    public ParticipationResponse updateParticipation(UUID participationId, UpdateParticipationRequest request) {
        Participation participation = findParticipationById(participationId);
        participation.updateMediaInfo(
                request.frameCount(),
                request.videoUrl(),
                request.thumbnailUrl()
        );
        return ParticipationResponse.of(participation);
    }
}
