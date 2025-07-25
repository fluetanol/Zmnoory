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
    private final String recommenderEmail;
    private final Long point;

    @Builder
    private MemberResponse(String email, String nickname, LocalDate birthday, Gender gender, String recommenderEmail, Long point) {
        this.email = email;
        this.nickname = nickname;
        this.birthday = birthday;
        this.gender = gender;
        this.recommenderEmail = recommenderEmail;
        this.point = point;
    }

    public static MemberResponse of(Member member) {
        return MemberResponse.builder()
                .email(member.getEmail())
                .nickname(member.getNickname())
                .birthday(member.getBirthday())
                .gender(member.getGender())
                .recommenderEmail(member.getRecommender() != null ? member.getRecommender().getEmail() : "추천인 없음")
                .point(member.getPoint())
                .build();
    }
}
