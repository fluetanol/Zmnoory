package com.gradation.zmnnoory.domain.member.service;

import com.gradation.zmnnoory.domain.member.dto.request.SignUpRequest;
import com.gradation.zmnnoory.domain.member.dto.response.MemberResponse;
import com.gradation.zmnnoory.domain.member.entity.Member;
import com.gradation.zmnnoory.domain.member.exception.DuplicatedEmailException;
import com.gradation.zmnnoory.domain.member.handler.MemberCreateHandler;
import com.gradation.zmnnoory.domain.member.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberCreateHandler memberCreateHandler;

    @Transactional
    public MemberResponse createMember(SignUpRequest signUpRequest) {
        if (memberRepository.existsByEmail(signUpRequest.email())) {
            throw new DuplicatedEmailException();
        }

        Member newMember = memberCreateHandler.createMemberWith(signUpRequest);
        memberRepository.save(newMember);
        return new MemberResponse(newMember.getEmail(), newMember.getGender());
    }

    public MemberResponse findById(Long id) {
        Member foundMember = memberRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("No User Found"));
        return new MemberResponse(foundMember.getEmail(), foundMember.getGender());
    }

    public List<MemberResponse> findAll() {
        return memberRepository.findAll().stream().map(m -> new MemberResponse(m.getEmail(), m.getGender())).toList();
    }
}
