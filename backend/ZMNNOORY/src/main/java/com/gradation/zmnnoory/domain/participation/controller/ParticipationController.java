package com.gradation.zmnnoory.domain.participation.controller;

import com.gradation.zmnnoory.common.dto.BaseResponse;
import com.gradation.zmnnoory.domain.participation.dto.ParticipationResponse;
import com.gradation.zmnnoory.domain.participation.dto.StartParticipationRequest;
import com.gradation.zmnnoory.domain.participation.dto.UpdateParticipationRequest;
import com.gradation.zmnnoory.domain.participation.entity.Participation;
import com.gradation.zmnnoory.domain.participation.service.ParticipationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/participations")
@Validated
public class ParticipationController {

    private final ParticipationService participationService;

    @PostMapping("/start")
    public BaseResponse<ParticipationResponse> startParticipation(
            @Valid @RequestBody StartParticipationRequest request) {
        
        Participation participation = participationService.startParticipation(
                request.getMemberId(), request.getStageId());
        return BaseResponse.<ParticipationResponse>builder()
                .status(HttpStatus.CREATED)
                .data(ParticipationResponse.from(participation))
                .build();
    }

    @PutMapping("/{participationId}/end")
    public BaseResponse<ParticipationResponse> endParticipation(
            @PathVariable @NotNull UUID participationId) {
        
        Participation participation = participationService.endParticipation(participationId);
        return BaseResponse.<ParticipationResponse>builder()
                .status(HttpStatus.OK)
                .data(ParticipationResponse.from(participation))
                .build();
    }

    @PutMapping("/{participationId}")
    public BaseResponse<ParticipationResponse> updateParticipation(
            @PathVariable @NotNull UUID participationId,
            @Valid @RequestBody UpdateParticipationRequest request) {
        
        Participation participation = participationService.updateParticipation(
                participationId, request);
        return BaseResponse.<ParticipationResponse>builder()
                .status(HttpStatus.OK)
                .data(ParticipationResponse.from(participation))
                .build();
    }

    @GetMapping("/check-first")
    public BaseResponse<Boolean> isFirstParticipation(
            @RequestParam @NotNull @Positive Long memberId,
            @RequestParam @NotNull @Positive Long stageId) {
        
        boolean isFirst = participationService.isFirstParticipation(memberId, stageId);
        return BaseResponse.<Boolean>builder()
                .status(HttpStatus.OK)
                .data(isFirst)
                .build();
    }

    @GetMapping("/{participationId}")
    public BaseResponse<ParticipationResponse> getParticipation(
            @PathVariable @NotNull UUID participationId) {
        
        Participation participation = participationService.getParticipation(participationId);
        return BaseResponse.<ParticipationResponse>builder()
                .status(HttpStatus.OK)
                .data(ParticipationResponse.from(participation))
                .build();
    }
}