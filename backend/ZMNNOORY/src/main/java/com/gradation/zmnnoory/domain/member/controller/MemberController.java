package com.gradation.zmnnoory.domain.member.controller;

import com.gradation.zmnnoory.domain.member.dto.SignUpRequest;
import com.gradation.zmnnoory.domain.member.entity.Member;
import com.gradation.zmnnoory.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/sign-up")
    public Member signUp(@RequestBody SignUpRequest signUpRequest) {
        return memberService.createMember(signUpRequest);
    }
}
