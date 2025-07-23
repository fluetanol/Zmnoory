package com.gradation.zmnnoory.domain.member.dto.response;

import com.gradation.zmnnoory.domain.member.entity.Gender;
import com.gradation.zmnnoory.domain.member.entity.Member;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class MemberResponse {

    private final String email;
    private final String nickname;
    private final LocalDate birthday;
    private final Gender gender;

    @Builder
    private MemberResponse(String email, String nickname, LocalDate birthday, Gender gender) {
        this.email = email;
        this.nickname = nickname;
        this.birthday = birthday;
        this.gender = gender;
    }

    public static MemberResponse of(Member member) {
        return MemberResponse.builder()
                .email(member.getEmail())
                .nickname(member.getNickname())
                .birthday(member.getBirthday())
                .gender(member.getGender())
                .build();
    }
}
