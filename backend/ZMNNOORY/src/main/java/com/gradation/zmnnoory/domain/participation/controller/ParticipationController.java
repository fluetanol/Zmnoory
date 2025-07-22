package com.gradation.zmnnoory.domain.participation.controller;

import com.gradation.zmnnoory.common.dto.BaseResponse;
import com.gradation.zmnnoory.domain.participation.dto.ParticipationResponse;
import com.gradation.zmnnoory.domain.participation.dto.StartParticipationRequest;
import com.gradation.zmnnoory.domain.participation.dto.UpdateParticipationRequest;
import com.gradation.zmnnoory.domain.participation.entity.Participation;
import com.gradation.zmnnoory.domain.participation.service.ParticipationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/participations")
public class ParticipationController {

    private final ParticipationService participationService;

    @PostMapping("/start")
    public BaseResponse<ParticipationResponse> startParticipation(
            @RequestBody StartParticipationRequest request) {
        
        Participation participation = participationService.startParticipation(
                request.getMemberId(), request.getStageId());
        return BaseResponse.<ParticipationResponse>builder()
                .status(HttpStatus.CREATED)
                .data(ParticipationResponse.of(participation))
                .build();
    }

    @PutMapping("/{participationId}/end")
    public BaseResponse<ParticipationResponse> endParticipation(
            @PathVariable UUID participationId) {
        
        Participation participation = participationService.endParticipation(participationId);
        return BaseResponse.<ParticipationResponse>builder()
                .status(HttpStatus.OK)
                .data(ParticipationResponse.of(participation))
                .build();
    }

    @PutMapping("/{participationId}")
    public BaseResponse<ParticipationResponse> updateParticipation(
            @PathVariable UUID participationId,
            @RequestBody UpdateParticipationRequest request) {
        
        Participation participation = participationService.updateParticipation(
                participationId, request);
        return BaseResponse.<ParticipationResponse>builder()
                .status(HttpStatus.OK)
                .data(ParticipationResponse.of(participation))
                .build();
    }

    @GetMapping("/check-first")
    public BaseResponse<Boolean> isFirstParticipation(
            @RequestParam Long memberId,
            @RequestParam Long stageId) {
        
        boolean isFirst = participationService.isFirstParticipation(memberId, stageId);
        return BaseResponse.<Boolean>builder()
                .status(HttpStatus.OK)
                .data(isFirst)
                .build();
    }

    @GetMapping("/{participationId}")
    public BaseResponse<ParticipationResponse> getParticipation(
            @PathVariable UUID participationId) {
        
        Participation participation = participationService.getParticipation(participationId);
        return BaseResponse.<ParticipationResponse>builder()
                .status(HttpStatus.OK)
                .data(ParticipationResponse.of(participation))
                .build();
    }
}