package com.gradation.zmnnoory.domain.member.handler;

import com.gradation.zmnnoory.domain.member.dto.request.SignUpRequest;
import com.gradation.zmnnoory.domain.member.entity.Member;
import com.gradation.zmnnoory.domain.member.resolver.PasswordResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberCreateHandler {

    private final PasswordResolver passwordResolver;

    public Member createMemberWith(SignUpRequest signUpRequest, Member recommender) {
        return Member.builder()
                .email(signUpRequest.email())
                .password(passwordResolver.encodePassword(signUpRequest.password()))
                .nickname(signUpRequest.nickname())
                .gender(signUpRequest.gender())
                .birthday(signUpRequest.birthday())
                .recommender(recommender)
                .build();
    }
}
