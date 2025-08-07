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
public class AdminInitializer implements ApplicationRunner {

    private final MemberRepository memberRepository;
    private final PasswordResolver passwordResolver;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Member adminUser = Member.builder()
                .email("admin@admin.com")
                .password(passwordResolver.encodePassword("123123123"))
                .nickname("adminUser")
                .gender(Gender.MALE)
                .birthday(LocalDate.now())
                .recommender(null)
                .role(Role.ADMIN)
                .build();
        memberRepository.save(adminUser);
    }
}
