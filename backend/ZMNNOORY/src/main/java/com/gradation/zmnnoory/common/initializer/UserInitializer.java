package com.gradation.zmnnoory.common.initializer;

import com.gradation.zmnnoory.domain.member.entity.Gender;
import com.gradation.zmnnoory.domain.member.entity.Member;
import com.gradation.zmnnoory.domain.member.entity.Role;
import com.gradation.zmnnoory.domain.member.repository.MemberRepository;
import com.gradation.zmnnoory.domain.member.resolver.PasswordResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class UserInitializer implements ApplicationRunner {

    private final MemberRepository memberRepository;
    private final PasswordResolver passwordResolver;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 이미 관리자 계정이 존재하는지 확인
        if (!memberRepository.existsByEmail("user@user.com")) {
            Member userUser = Member.builder()
                    .email("user@user.com")
                    .password(passwordResolver.encodePassword("123123123"))
                    .nickname("userUser")
                    .gender(Gender.MALE)
                    .birthday(LocalDate.now())
                    .optionalConsent(true)
                    .recommender(null)
                    .role(Role.USER)
                    .build();
            memberRepository.save(userUser);
        }
    }
}
