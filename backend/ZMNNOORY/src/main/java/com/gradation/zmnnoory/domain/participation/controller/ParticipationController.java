package com.gradation.zmnnoory.domain.participation.controller;

import com.gradation.zmnnoory.common.dto.BaseResponse;
import com.gradation.zmnnoory.domain.participation.dto.request.EndParticipationRequest;
import com.gradation.zmnnoory.domain.participation.dto.response.ParticipationResponse;
import com.gradation.zmnnoory.domain.participation.dto.request.StartParticipationRequest;
import com.gradation.zmnnoory.domain.participation.service.ParticipationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "게임 참여 API", description = "게임 참여 시작, 게임 참여 종료 및 리워드 지급, 특정 멤버의 모든 참여 조회")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/participations")
@Validated
public class ParticipationController {

    private final ParticipationService participationService;

    // 1. 게임 참여 시작
    @Operation(
            summary = "게임 참여 시작",
            description = """
            사용자가 특정 게임에 참여를 시작합니다.
            - 요청 본문에는 사용자 이메일과 게임 제목을 포함해야 합니다.
            - 이미 참여한 경우 예외가 발생할 수 있습니다.
            """
    )
    @PostMapping("/start")
    public BaseResponse<ParticipationResponse> startParticipation(
            @Valid @RequestBody StartParticipationRequest request) {

        return BaseResponse.<ParticipationResponse>builder()
                .status(HttpStatus.CREATED)
                .data(participationService.startParticipation(request))
                .build();
    }

    // 2. 게임 참여 종료 및 리워드 지급
    @Operation(
            summary = "게임 참여 종료 및 리워드 지급",
            description = """
            사용자의 게임 참여를 완료 처리하고, 처음 참여한 경우 리워드를 지급합니다.
            - 이미 완료된 참여인 경우, 중복 지급은 방지됩니다.
            """
    )
    @PutMapping("/end")
    public BaseResponse<ParticipationResponse> endParticipation(
            @Valid @RequestBody EndParticipationRequest request) {

        return BaseResponse.<ParticipationResponse>builder()
                .status(HttpStatus.OK)
                .data(participationService.endParticipation(request))
                .build();
    }

    // 3. 특정 멤버의 모든 참여 조회
    @Operation(
            summary = "특정 멤버의 모든 참여 내역 조회",
            description = """
            특정 사용자(memberId)의 모든 참여 정보를 조회합니다.
            - 참여한 게임, 상태(NOT_PARTICIPATED, COMPLETED) 등의 정보를 포함합니다.
            - 존재하지 않는 사용자 ID의 경우 404 에러가 발생할 수 있습니다.
            """
    )
    @GetMapping("/member/{memberId}")
    public BaseResponse<List<ParticipationResponse>> getParticipationsByMember(
            @PathVariable Long memberId) {

        return BaseResponse.<List<ParticipationResponse>>builder()
                .status(HttpStatus.OK)
                .data(participationService.getParticipationsByMember(memberId))
                .build();
    }
}
