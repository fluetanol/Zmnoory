package com.gradation.zmnnoory.domain.member.dto.response;

import com.gradation.zmnnoory.domain.member.entity.Gender;
import com.gradation.zmnnoory.domain.member.entity.Member;
import com.gradation.zmnnoory.domain.member.entity.Role;
import jakarta.persistence.EntityNotFoundException;
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
        String recommenderNickname;
        try {
            // member.getRecommender()가 프록시 객체일 수 있으며, 실제 데이터 접근 시 예외 발생 가능
            recommenderNickname = member.getRecommender() != null ? member.getRecommender().getNickname() : "추천인 없음";
        } catch (EntityNotFoundException e) {
            // 추천인 ID는 존재하지만, 실제 해당 회원이 DB에 없는 경우(데이터 정합성 문제)
            recommenderNickname = "알 수 없는 추천인";
        }

        return MemberResponse.builder()
                .email(member.getEmail())
                .nickname(member.getNickname())
                .birthday(member.getBirthday())
                .gender(member.getGender())
                .recommenderNickname(recommenderNickname)
                .point(member.getPoint())
                .role(member.getRole())
                .build();
    }
}
