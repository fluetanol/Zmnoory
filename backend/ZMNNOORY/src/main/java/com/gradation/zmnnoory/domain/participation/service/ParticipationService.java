package com.gradation.zmnnoory.domain.participation.service;

import com.gradation.zmnnoory.domain.game.entity.Game;
import com.gradation.zmnnoory.domain.game.repository.GameRepository;
import com.gradation.zmnnoory.domain.member.entity.Member;
import com.gradation.zmnnoory.domain.member.exception.MemberNotFoundException;
import com.gradation.zmnnoory.domain.member.repository.MemberRepository;
import com.gradation.zmnnoory.domain.participation.dto.ParticipationResponse;
import com.gradation.zmnnoory.domain.participation.dto.UpdateParticipationRequest;
import com.gradation.zmnnoory.domain.participation.entity.Participation;
import com.gradation.zmnnoory.domain.participation.repository.ParticipationRepository;
import com.gradation.zmnnoory.domain.participation.status.ParticipationStatus;
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
    private final GameRepository gameRepository;

    public ParticipationResponse startParticipation(String email, String gameTitle) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(MemberNotFoundException::new);
        
        Game game = gameRepository.findByTitle(gameTitle)
                .orElseThrow(MemberNotFoundException::new);

        Participation participation = Participation.builder()
                .member(member)
                .game(game)
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

    public boolean isFirstParticipation(Long memberId, Long gameId) {
        return !participationRepository.existsByMemberIdAndGameId(memberId, gameId);
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
