package com.gradation.zmnnoory.domain.member.controller;

import com.gradation.zmnnoory.common.dto.BaseResponse;
import com.gradation.zmnnoory.domain.member.dto.MemberUpdateRequest;
import com.gradation.zmnnoory.domain.member.dto.request.SignUpRequest;
import com.gradation.zmnnoory.domain.member.dto.response.MemberResponse;
import com.gradation.zmnnoory.domain.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 모든 응답은 common.handler.ApiResponseHandler 에서 후처리됨
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
@Tag(name = "사용자 API", description = "사용자 관련 API")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/sign-up")
    @Operation(summary = "회원가입", description = "회원가입 API")
    public BaseResponse<MemberResponse> signUp(@RequestBody @Validated SignUpRequest signUpRequest) {
        return BaseResponse.<MemberResponse>builder()
                .status(HttpStatus.CREATED)
                .data(memberService.createMember(signUpRequest))
                .build();
    }

    @GetMapping("/{memberId}")
    public BaseResponse<MemberResponse> getMembers(@PathVariable("memberId") Long memberId) {
        return BaseResponse.<MemberResponse>builder()
                .status(HttpStatus.OK)
                .data(memberService.findById(memberId))
                .build();
    }

    @GetMapping
    public BaseResponse<List<MemberResponse>> getMembers() {
        return BaseResponse.<List<MemberResponse>>builder()
                .status(HttpStatus.ACCEPTED)
                .data(memberService.findAll())
                .build();
    }

    @PutMapping("/{email}")
    public BaseResponse<MemberResponse> updateUserInfo(
            @PathVariable String email,
            @Validated MemberUpdateRequest memberUpdateRequest
    ) {
        return BaseResponse.<MemberResponse>builder()
                .status(HttpStatus.OK)
                .message("업데이트 성공")
                .data(memberService.updateUserInfoWith(email, memberUpdateRequest))
                .build();
    }
    
}
