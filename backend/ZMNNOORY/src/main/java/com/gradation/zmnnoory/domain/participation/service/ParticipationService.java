package com.gradation.zmnnoory.domain.participation.service;

import com.gradation.zmnnoory.domain.game.entity.Game;
import com.gradation.zmnnoory.domain.game.repository.GameRepository;
import com.gradation.zmnnoory.domain.member.entity.Member;
import com.gradation.zmnnoory.domain.member.exception.MemberNotFoundException;
import com.gradation.zmnnoory.domain.game.exception.GameNotFoundException;
import com.gradation.zmnnoory.domain.participation.exception.AlreadyParticipatedException;
import com.gradation.zmnnoory.domain.member.repository.MemberRepository;
import com.gradation.zmnnoory.domain.participation.dto.EndParticipationRequest;
import com.gradation.zmnnoory.domain.participation.dto.ParticipationResponse;
import com.gradation.zmnnoory.domain.participation.dto.StartParticipationRequest;
import com.gradation.zmnnoory.domain.participation.entity.Participation;
import com.gradation.zmnnoory.domain.participation.entity.ParticipationStatus;
import com.gradation.zmnnoory.domain.participation.exception.ParticipationNotFoundException;
import com.gradation.zmnnoory.domain.participation.repository.ParticipationRepository;
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

    // 2. 게임 참여 종료 및 리워드 지급
    public ParticipationResponse endParticipation(EndParticipationRequest request) {
        Participation participation = participationRepository
                .findByMemberEmailAndGameTitle(request.email(), request.gameTitle())
                .orElseThrow(ParticipationNotFoundException::new);

        if (participation.getStatus() == ParticipationStatus.NOT_PARTICIPATED) {
            participation.complete();
//          Long rewardPoint = participation.getGame().getPoint();
//          Member member = participation.getMember();
//          member.addPoint(rewardPoint);
        }

        return ParticipationResponse.of(participation);
    }

    // 3. 한 멤버의 전체 참여 리스트
    public List<ParticipationResponse> getParticipationsByMember(Long memberId) {
        return participationRepository.findByMemberId(memberId).stream()
                .map(ParticipationResponse::of)
                .toList();
    }

}
