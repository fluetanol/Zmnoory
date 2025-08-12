package com.gradation.zmnnoory.domain.member.dto.response;

import com.gradation.zmnnoory.domain.member.entity.Gender;
import com.gradation.zmnnoory.domain.member.entity.Member;
import com.gradation.zmnnoory.domain.member.entity.Role;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class MemberResponse {

    private final String email;
    private final String nickname;
    private final LocalDate birthday;
    private final Gender gender;
    private final String recommenderNickname;
    private final Long point;
    private final Role role;

    @Builder
    private MemberResponse(String email, String nickname, LocalDate birthday, Gender gender, String recommenderNickname, Long point, Role role) {
        this.email = email;
        this.nickname = nickname;
        this.birthday = birthday;
        this.gender = gender;
        this.recommenderNickname = recommenderNickname;
        this.point = point;
        this.role = role;
    }

    public static MemberResponse from(Member member) {
        return MemberResponse.builder()
                .email(member.getEmail())
                .nickname(member.getNickname())
                .birthday(member.getBirthday())
                .gender(member.getGender())
                .recommenderNickname(member.getRecommender() != null ? member.getRecommender().getNickname() : "추천인 없음")
                .point(member.getPoint())
                .role(member.getRole())
                .build();
    }
}
