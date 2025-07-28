package com.gradation.zmnnoory.domain.member.aspect;

import com.gradation.zmnnoory.domain.member.entity.Member;
import com.gradation.zmnnoory.domain.member.entity.Role;
import com.gradation.zmnnoory.domain.member.exception.MemberRoleException;
import com.gradation.zmnnoory.domain.member.exception.NoLoginInfoException;
import com.gradation.zmnnoory.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminOnlyAspect {

    private final MemberService memberService;

    @Before("@annotation(com.gradation.zmnnoory.domain.member.annotation.AdminOnly)")
    public void checkAdminRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new NoLoginInfoException();
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof UserDetails loginInfo)) {
            throw new IllegalStateException("인증 정보가 올바르지 않습니다.");
        }

        Member member = memberService.findByEmail(loginInfo.getUsername());

        if (member.getRole() == Role.USER) {
            throw new MemberRoleException();
        }
    }
}
