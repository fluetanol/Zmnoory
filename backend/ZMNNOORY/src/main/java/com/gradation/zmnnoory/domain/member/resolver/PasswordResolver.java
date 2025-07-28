package com.gradation.zmnnoory.domain.member.resolver;

import com.gradation.zmnnoory.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PasswordResolver {

    private final PasswordEncoder passwordEncoder;

    public boolean isInvalidPasswordOf(Member member, String password) {
        return !passwordEncoder.matches(password, member.getPassword());
    }

    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
}
