package com.gradation.zmnnoory.domain.member.dto.response;

import com.gradation.zmnnoory.domain.member.entity.Gender;
import com.gradation.zmnnoory.domain.member.entity.Member;
import com.gradation.zmnnoory.domain.member.entity.Role;

import java.time.LocalDate;

public record MeResponse(
        Long id,
        String email,
        String nickname,
        LocalDate birthday,
        Gender gender,
        Long point,
        Role role,
        String profileImageUrl,
        Boolean optionalConsent
) {
    public static MeResponse of(Member m) {
        return new MeResponse(
                m.getId(),
                m.getEmail(),
                m.getNickname(),
                m.getBirthday(),
                m.getGender(),
                m.getPoint(),
                m.getRole(),
                m.getProfileImageUrl(),
                m.isOptionalConsent()
        );
    }
}