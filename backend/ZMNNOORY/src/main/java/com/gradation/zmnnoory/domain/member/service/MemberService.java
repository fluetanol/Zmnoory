package com.gradation.zmnnoory.domain.member.service;

import com.gradation.zmnnoory.domain.member.dto.SignUpRequest;
import com.gradation.zmnnoory.domain.member.entity.Member;
import com.gradation.zmnnoory.domain.member.handler.MemberCreateHandler;
import com.gradation.zmnnoory.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberCreateHandler memberCreateHandler;

    @Transactional
    public Member createMember(SignUpRequest signUpRequest) {
        Member newMember = memberCreateHandler.createMemberWith(signUpRequest);
        memberRepository.save(newMember);
        return newMember;
    }
}
