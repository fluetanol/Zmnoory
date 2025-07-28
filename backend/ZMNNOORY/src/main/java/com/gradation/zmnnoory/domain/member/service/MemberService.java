package com.gradation.zmnnoory.domain.member.service;

import com.gradation.zmnnoory.domain.member.dto.MemberUpdateRequest;
import com.gradation.zmnnoory.domain.member.dto.request.PasswordUpdateRequest;
import com.gradation.zmnnoory.domain.member.dto.request.SignUpRequest;
import com.gradation.zmnnoory.domain.member.dto.response.MemberResponse;
import com.gradation.zmnnoory.domain.member.entity.Member;
import com.gradation.zmnnoory.domain.member.exception.DuplicatedEmailException;
import com.gradation.zmnnoory.domain.member.exception.InvalidPasswordException;
import com.gradation.zmnnoory.domain.member.exception.MemberNotFoundException;
import com.gradation.zmnnoory.domain.member.handler.MemberCreateHandler;
import com.gradation.zmnnoory.domain.member.repository.MemberRepository;
import com.gradation.zmnnoory.domain.member.resolver.PasswordResolver;
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
    private final PasswordResolver passwordResolver;

    @Transactional
    public MemberResponse createMember(SignUpRequest signUpRequest) {
        if (memberRepository.existsByEmail(signUpRequest.email())) {
            throw new DuplicatedEmailException();
        }

        Member recommender = null;
        String recommenderEmail = signUpRequest.recommenderEmail();

        if (recommenderEmail != null && !recommenderEmail.isBlank()) {
            recommender = memberRepository.findByEmail(recommenderEmail)
                    .orElseThrow(() -> new MemberNotFoundException("추천인을 찾을 수 없습니다."));
        }

        Member newMember = memberCreateHandler.createMemberWith(signUpRequest, recommender);
        memberRepository.save(newMember);
        return MemberResponse.of(newMember);
    }

    public MemberResponse findById(Long id) {
        Member foundMember = memberRepository.findById(id).orElseThrow(MemberNotFoundException::new);
        return MemberResponse.of(foundMember);
    }

    public List<MemberResponse> findAll() {
        return memberRepository.findAll().stream().map(MemberResponse::of).toList();
    }

    @Transactional
    public MemberResponse updateMemberInfoWith(String email, MemberUpdateRequest memberUpdateRequest) {
        Member member = memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);
        member.update(memberUpdateRequest);
        return MemberResponse.of(member);
    }

    @Transactional
    public MemberResponse updateMemberPassword(Member member, PasswordUpdateRequest passwordUpdateRequest) {
        if (passwordResolver.isInvalidPasswordOf(member, passwordUpdateRequest.getOriginPassword())) {
            throw new InvalidPasswordException();
        }

        String newPassword = passwordResolver.encodePassword(passwordUpdateRequest.getUpdatedPassword());
        member.updatePassword(newPassword);

        return MemberResponse.of(member);
    }

    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);
    }

    public MemberResponse updateMemberRole(Long targetId) {
        Member member = memberRepository.findById(targetId).orElseThrow(MemberNotFoundException::new);
        member.updateRole();
        return MemberResponse.of(member);
    }
}
