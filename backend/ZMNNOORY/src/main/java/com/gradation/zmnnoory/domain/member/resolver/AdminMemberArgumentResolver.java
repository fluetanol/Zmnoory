package com.gradation.zmnnoory.domain.member.resolver;

import com.gradation.zmnnoory.domain.member.annotation.AdminOnly;
import com.gradation.zmnnoory.domain.member.entity.Member;
import com.gradation.zmnnoory.domain.member.entity.Role;
import com.gradation.zmnnoory.domain.member.exception.MemberRoleException;
import com.gradation.zmnnoory.domain.member.exception.NoLoginInfoException;
import com.gradation.zmnnoory.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@RequiredArgsConstructor
public class AdminMemberArgumentResolver implements HandlerMethodArgumentResolver {

    private final MemberService memberService;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(AdminOnly.class)
                && parameter.getParameterType().equals(Member.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory
    ) throws Exception {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new NoLoginInfoException();
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof UserDetails loginMember)) {
            throw new IllegalStateException("인증 정보가 올바르지 않습니다.");
        }

        Member member = memberService.findByEmail(loginMember.getUsername());

        if (member.getRole() == Role.USER) {
            throw new MemberRoleException();
        }

        return member;
    }
}
