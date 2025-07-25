package com.gradation.zmnnoory.domain.participation.controller;

import com.gradation.zmnnoory.common.dto.BaseResponse;
import com.gradation.zmnnoory.domain.participation.dto.EndParticipationRequest;
import com.gradation.zmnnoory.domain.participation.dto.ParticipationResponse;
import com.gradation.zmnnoory.domain.participation.dto.StartParticipationRequest;
import com.gradation.zmnnoory.domain.participation.service.ParticipationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/participations")
@Validated
public class ParticipationController {

    private final ParticipationService participationService;

    // 1. 게임 참여 시작
    @PostMapping("/start")
    public BaseResponse<ParticipationResponse> startParticipation(
            @Valid @RequestBody StartParticipationRequest request) {

        return BaseResponse.<ParticipationResponse>builder()
                .status(HttpStatus.CREATED)
                .data(participationService.startParticipation(request))
                .build();
    }

    // 2. 게임 참여 종료 및 리워드 지급
    @PutMapping("/end")
    public BaseResponse<ParticipationResponse> endParticipation(
            @Valid @RequestBody EndParticipationRequest request) {

        return BaseResponse.<ParticipationResponse>builder()
                .status(HttpStatus.OK)
                .data(participationService.endParticipation(request))
                .build();
    }

    // 3. 특정 멤버의 모든 참여 조회
    @GetMapping("/member/{memberId}")
    public BaseResponse<List<ParticipationResponse>> getParticipationsByMember(
            @PathVariable Long memberId) {

        return BaseResponse.<List<ParticipationResponse>>builder()
                .status(HttpStatus.OK)
                .data(participationService.getParticipationsByMember(memberId))
                .build();
    }
}
