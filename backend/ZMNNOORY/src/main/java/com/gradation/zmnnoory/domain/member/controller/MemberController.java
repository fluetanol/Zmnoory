package com.gradation.zmnnoory.domain.member.controller;

import com.gradation.zmnnoory.common.dto.BaseResponse;
import com.gradation.zmnnoory.domain.member.dto.request.SignUpRequest;
import com.gradation.zmnnoory.domain.member.dto.response.MemberResponse;
import com.gradation.zmnnoory.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 모든 응답은 common.handler.ApiResponseHandler 에서 후처리됨
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/sign-up")
    public BaseResponse<MemberResponse> signUp(@RequestBody SignUpRequest signUpRequest) {
        return BaseResponse.<MemberResponse>builder()
                .status(HttpStatus.CREATED)
                .data(memberService.createMember(signUpRequest))
                .build();
    }

    @GetMapping("/{memberId}")
    public BaseResponse<MemberResponse> getMember5(@PathVariable("memberId") Long memberId) {
        return BaseResponse.<MemberResponse>builder()
                .status(HttpStatus.OK)
                .data(memberService.findById(memberId))
                .build();
    }

    @GetMapping("/members")
    public BaseResponse<List<MemberResponse>> getMembers() {
        return BaseResponse.<List<MemberResponse>>builder()
                .status(HttpStatus.ACCEPTED)
                .data(memberService.findAll())
                .build();
    }
}
