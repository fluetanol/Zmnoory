package com.gradation.zmnnoory.domain.participation.controller;

import com.gradation.zmnnoory.common.dto.BaseResponse;
import com.gradation.zmnnoory.domain.member.annotation.LoginMember;
import com.gradation.zmnnoory.domain.member.entity.Member;
import com.gradation.zmnnoory.domain.participation.dto.request.CompleteParticipationRequest;
import com.gradation.zmnnoory.domain.participation.dto.request.PresignedUrlRequest;
import com.gradation.zmnnoory.domain.participation.dto.request.StartParticipationRequest;
import com.gradation.zmnnoory.domain.participation.dto.response.ParticipationResponse;
import com.gradation.zmnnoory.domain.participation.dto.response.PresignedUrlResponse;
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

    // 2. 영상 업로드용 Presigned URL 생성
    @Operation(
            summary = "영상 업로드용 Presigned URL 생성",
            description = """
            참여한 게임에 대한 영상 업로드용 S3 Presigned URL을 생성합니다.
            - 이미 완료된 참여에 대해서는 URL을 생성할 수 없습니다.
            - 생성된 URL은 제한된 시간 동안만 유효합니다.
            """
    )
    @PostMapping("/presigned-url")
    public BaseResponse<PresignedUrlResponse> getPresignedUrl(
            @Valid @RequestBody PresignedUrlRequest request) {

        return BaseResponse.<PresignedUrlResponse>builder()
                .status(HttpStatus.OK)
                .data(participationService.getPresignedUrl(request))
                .build();
    }

    // 3. 참여 완료 처리 (업로드 성공 후)
    @Operation(
            summary = "참여 완료 및 리워드 지급",
            description = """
            영상 업로드 완료 후 참여를 완료 처리하고 리워드를 지급합니다.
            - 업로드 완료된 영상 URL과 메타데이터를 포함해야 합니다.
            - 이미 완료된 참여에 대해서는 중복 처리를 방지합니다.
            """
    )
    @PutMapping("/complete")
    public BaseResponse<ParticipationResponse> completeParticipation(
            @Valid @RequestBody CompleteParticipationRequest request) {

        return BaseResponse.<ParticipationResponse>builder()
                .status(HttpStatus.OK)
                .data(participationService.completeParticipation(request))
                .build();
    }


    // 4. 특정 멤버의 모든 참여 조회
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
    
    // 5. 로그인한 멤버의 모든 참여 조회
    @Operation(
            summary = "로그인한 멤버의 모든 참여 내역 조회",
            description = """
            현재 로그인한 사용자의 모든 참여 정보를 조회합니다.
            - 참여한 게임, 상태(NOT_PARTICIPATED, COMPLETED) 등의 정보를 포함합니다.
            - 로그인하지 않은 경우 인증 에러가 발생할 수 있습니다.
            """
    )
    @GetMapping("/member/me")
    public BaseResponse<List<ParticipationResponse>> getMyParticipations(
            @LoginMember Member member) {

        return BaseResponse.<List<ParticipationResponse>>builder()
                .status(HttpStatus.OK)
                .data(participationService.getParticipationsByMember(member.getId()))
                .build();
    }
}
