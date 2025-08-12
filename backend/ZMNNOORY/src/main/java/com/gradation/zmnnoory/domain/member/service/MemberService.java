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
        String recommenderNickname = signUpRequest.recommenderNickname();

        if (recommenderNickname != null && !recommenderNickname.isBlank()) {
            recommender = memberRepository.findByNickname(recommenderNickname)
                    .orElseThrow(() -> new MemberNotFoundException("추천인을 찾을 수 없습니다."));
        }

        Member newMember = memberCreateHandler.createMemberWith(signUpRequest, recommender);
        memberRepository.save(newMember);
        
        // Member 저장 후 추천인 보상 처리
        memberCreateHandler.processReferralRewardFor(recommender, newMember);
        
        return MemberResponse.from(newMember);
    }

    public MemberResponse findById(Long id) {
        Member foundMember = memberRepository.findById(id).orElseThrow(MemberNotFoundException::new);
        return MemberResponse.from(foundMember);
    }

    public List<MemberResponse> findAll() {
        return memberRepository.findAll().stream().map(MemberResponse::from).toList();
    }

    @Transactional
    public MemberResponse updateMemberInfoWith(String email, MemberUpdateRequest memberUpdateRequest) {
        Member member = memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);
        member.update(memberUpdateRequest);
        return MemberResponse.from(member);
    }

    @Transactional
    public MemberResponse updateMemberPassword(Member member, PasswordUpdateRequest passwordUpdateRequest) {
        if (passwordResolver.isInvalidPasswordOf(member, passwordUpdateRequest.getOriginPassword())) {
            throw new InvalidPasswordException();
        }

        String newPassword = passwordResolver.encodePassword(passwordUpdateRequest.getUpdatedPassword());
        member.updatePassword(newPassword);

        return MemberResponse.from(member);
    }

    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);
    }

    public MemberResponse updateMemberRole(Long targetId) {
        Member member = memberRepository.findById(targetId).orElseThrow(MemberNotFoundException::new);
        member.updateRole();
        return MemberResponse.from(member);
    }

    /**
     * 닉네임 사용 가능 여부 반환
     */
    public boolean isNicknameAvailable(String nickname) {
        return !memberRepository.existsByNickname(nickname);
    }

    /**
     * 이메일 사용 가능 여부 반환
     */
    public boolean isEmailAvailable(String email) {
        return !memberRepository.existsByEmail(email);
    }

    public MemberResponse myInfo(Member me) {
        return MemberResponse.from(me);
    }

//    @PostConstruct
//    @Transactional
//    protected void initAdmin() {
//        Member adminUser = Member.builder()
//                .email("admin@admin.com")
//                .password(passwordResolver.encodePassword("123123123"))
//                .nickname("adminUser")
//                .gender(Gender.MALE)
//                .birthday(LocalDate.now())
//                .recommender(null)
//                .role(Role.ADMIN)
//                .build();
//        memberRepository.save(adminUser);
//    }
}
