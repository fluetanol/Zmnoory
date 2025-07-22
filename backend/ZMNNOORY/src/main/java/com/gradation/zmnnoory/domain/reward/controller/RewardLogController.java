package com.gradation.zmnnoory.domain.reward.controller;

import com.gradation.zmnnoory.common.dto.BaseResponse;
import com.gradation.zmnnoory.domain.reward.dto.RewardLogRequest;
import com.gradation.zmnnoory.domain.reward.dto.RewardLogResponse;
import com.gradation.zmnnoory.domain.reward.entity.RewardLog;
import com.gradation.zmnnoory.domain.reward.service.RewardLogService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rewards")
@Validated
public class RewardLogController {

    private final RewardLogService rewardLogService;

    @PostMapping
    public BaseResponse<RewardLogResponse> giveReward(@Valid @RequestBody RewardLogRequest request) {
        RewardLog rewardLog = rewardLogService.giveReward(request.getParticipationId());
        
        return BaseResponse.<RewardLogResponse>builder()
                .status(HttpStatus.CREATED)
                .data(RewardLogResponse.from(rewardLog))
                .build();
    }

    @GetMapping("/member/{memberId}")
    public BaseResponse<List<RewardLogResponse>> getRewardLogsByMember(
            @PathVariable @NotNull @Positive Long memberId) {
        List<RewardLog> rewardLogs = rewardLogService.getRewardLogsByMember(memberId);
        List<RewardLogResponse> responses = rewardLogs.stream()
                .map(RewardLogResponse::from)
                .toList();
        
        return BaseResponse.<List<RewardLogResponse>>builder()
                .status(HttpStatus.OK)
                .data(responses)
                .build();
    }

    @GetMapping("/check-reward")
    public BaseResponse<Boolean> hasReceivedReward(
            @RequestParam @NotNull @Positive Long memberId,
            @RequestParam @NotNull @Positive Long stageId) {
        
        boolean hasReceived = rewardLogService.hasReceivedReward(memberId, stageId);
        
        return BaseResponse.<Boolean>builder()
                .status(HttpStatus.OK)
                .data(hasReceived)
                .build();
    }

    @GetMapping("/{rewardId}")
    public BaseResponse<RewardLogResponse> getRewardLog(@PathVariable @NotNull UUID rewardId) {
        RewardLog rewardLog = rewardLogService.findById(rewardId);
        
        return BaseResponse.<RewardLogResponse>builder()
                .status(HttpStatus.OK)
                .data(RewardLogResponse.from(rewardLog))
                .build();
    }

    @GetMapping
    public BaseResponse<List<RewardLogResponse>> getAllRewardLogs() {
        List<RewardLog> rewardLogs = rewardLogService.findAll();
        List<RewardLogResponse> responses = rewardLogs.stream()
                .map(RewardLogResponse::from)
                .toList();
        
        return BaseResponse.<List<RewardLogResponse>>builder()
                .status(HttpStatus.OK)
                .data(responses)
                .build();
    }
}
