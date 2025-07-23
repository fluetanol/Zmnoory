package com.gradation.zmnnoory.domain.reward.controller;

import com.gradation.zmnnoory.common.dto.BaseResponse;
import com.gradation.zmnnoory.domain.reward.dto.RewardLogRequest;
import com.gradation.zmnnoory.domain.reward.dto.RewardLogResponse;
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
        return BaseResponse.<RewardLogResponse>builder()
                .status(HttpStatus.CREATED)
                .data(rewardLogService.giveReward(request.participationId()))
                .build();
    }

    @GetMapping("/member/{memberId}")
    public BaseResponse<List<RewardLogResponse>> getRewardLogsByMember(
            @PathVariable Long memberId) {
        return BaseResponse.<List<RewardLogResponse>>builder()
                .status(HttpStatus.OK)
                .data(rewardLogService.getRewardLogsByMember(memberId))
                .build();
    }

    @GetMapping("/check-reward")
    public BaseResponse<Boolean> hasReceivedReward(
            @RequestParam Long memberId,
            @RequestParam Long stageId) {
        
        boolean hasReceived = rewardLogService.hasReceivedReward(memberId, stageId);
        
        return BaseResponse.<Boolean>builder()
                .status(HttpStatus.OK)
                .data(hasReceived)
                .build();
    }

    @GetMapping("/{rewardId}")
    public BaseResponse<RewardLogResponse> getRewardLog(@PathVariable UUID rewardId) {
        return BaseResponse.<RewardLogResponse>builder()
                .status(HttpStatus.OK)
                .data(rewardLogService.findById(rewardId))
                .build();
    }

    @GetMapping
    public BaseResponse<List<RewardLogResponse>> getAllRewardLogs() {
        return BaseResponse.<List<RewardLogResponse>>builder()
                .status(HttpStatus.OK)
                .data(rewardLogService.findAll())
                .build();
    }
}
