package com.gradation.zmnnoory.domain.participation.controller;

import com.gradation.zmnnoory.domain.participation.dto.ParticipationResponse;
import com.gradation.zmnnoory.domain.participation.dto.StartParticipationRequest;
import com.gradation.zmnnoory.domain.participation.dto.UpdateParticipationRequest;
import com.gradation.zmnnoory.domain.participation.entity.Participation;
import com.gradation.zmnnoory.domain.participation.service.ParticipationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/participations")
public class ParticipationController {

    private final ParticipationService participationService;

    @PostMapping("/start")
    public ResponseEntity<ParticipationResponse> startParticipation(
            @RequestBody StartParticipationRequest request) {
        
        Participation participation = participationService.startParticipation(
                request.getMemberId(), request.getStageId());
        return ResponseEntity.ok(ParticipationResponse.from(participation));
    }

    @PutMapping("/{participationId}/end")
    public ResponseEntity<ParticipationResponse> endParticipation(
            @PathVariable UUID participationId) {
        
        Participation participation = participationService.endParticipation(participationId);
        return ResponseEntity.ok(ParticipationResponse.from(participation));
    }

    @PutMapping("/{participationId}")
    public ResponseEntity<ParticipationResponse> updateParticipation(
            @PathVariable UUID participationId,
            @RequestBody UpdateParticipationRequest request) {
        
        Participation participation = participationService.updateParticipation(
                participationId, request);
        return ResponseEntity.ok(ParticipationResponse.from(participation));
    }

    @GetMapping("/check-first")
    public ResponseEntity<Boolean> isFirstParticipation(
            @RequestParam Long memberId,
            @RequestParam Long stageId) {
        
        boolean isFirst = participationService.isFirstParticipation(memberId, stageId);
        return ResponseEntity.ok(isFirst);
    }

    @GetMapping("/{participationId}")
    public ResponseEntity<ParticipationResponse> getParticipation(
            @PathVariable UUID participationId) {
        
        Participation participation = participationService.getParticipation(participationId);
        return ResponseEntity.ok(ParticipationResponse.from(participation));
    }
}