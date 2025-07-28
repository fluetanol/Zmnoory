package com.gradation.zmnnoory.domain.member.service;

import com.gradation.zmnnoory.domain.member.dto.MemberUpdateRequest;
import com.gradation.zmnnoory.domain.member.dto.request.PasswordUpdateRequest;
import com.gradation.zmnnoory.domain.member.dto.response.MemberResponse;
import com.gradation.zmnnoory.domain.member.entity.Gender;
import com.gradation.zmnnoory.domain.member.entity.Member;
import com.gradation.zmnnoory.domain.member.exception.InvalidPasswordException;
import com.gradation.zmnnoory.domain.member.exception.MemberNotFoundException;
import com.gradation.zmnnoory.domain.member.repository.MemberRepository;
import com.gradation.zmnnoory.domain.member.resolver.PasswordResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest
class MemberServiceTest {

    @Autowired
    MemberService memberService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PasswordResolver passwordResolver;

    @BeforeEach
    void setup() {
        Member member = Member.builder()
                .email("test@test.com")
                .password(passwordResolver.encodePassword("123123123"))
                .gender(Gender.MALE)
                .birthday(LocalDate.of(2000, 1, 1))
                .nickname("tester")
                .build();

        memberRepository.save(member);
    }

    @Nested
    @DisplayName("회원 정보 업데이트 테스트")
    class MemberUpdateTest {

        @Test
        @DisplayName("존재하는 회원 정보 업데이트 테스트")
        void updateMemberTest() {
            // given
            Member member = memberRepository.findByEmail("test@test.com").orElseThrow();

            MemberUpdateRequest updateRequest = new MemberUpdateRequest(
                    "updated@test.com",
                    Gender.FEMALE,
                    "updatedNickname",
                    LocalDate.of(1995, 5, 15)
            );

            // when
            MemberResponse updatedMember = memberService.updateUserInfoWith(member.getEmail(), updateRequest);

            // then
            assertThat(updatedMember.getEmail()).isEqualTo(updateRequest.email());
            assertThat(updatedMember.getNickname()).isEqualTo(updateRequest.nickname());
            assertThat(updatedMember.getGender()).isEqualTo(updateRequest.gender());
            assertThat(updatedMember.getBirthday()).isEqualTo(updateRequest.birthday());
        }

        @Test
        @DisplayName("존재하지 않는 회원 정보 업데이트 테스트")
        void updateNonExistentMemberTest() {
            // given
            String nonExistentMemberEmail = "noUser@email.com"; // 존재하지 않는 회원 ID

            MemberUpdateRequest updateRequest = new MemberUpdateRequest(
                    "updated@test.com",
                    Gender.FEMALE,
                    "updatedNickname",
                    LocalDate.of(1995, 5, 15)
            );

            // when & then
            assertThrows(MemberNotFoundException.class, () -> {
                memberService.updateUserInfoWith(nonExistentMemberEmail, updateRequest);
            });
        }
    }

    @Nested
    @DisplayName("비밀번호 업데이트 테스트")
    class PasswordUpdateTest {

        @Test
        @DisplayName("비밀번호를 업데이트할 수 있다.")
        void passwordUpdateTestSuccess() {
            // given
            Member member = memberRepository.findByEmail("test@test.com").get();
            PasswordUpdateRequest request = new PasswordUpdateRequest("123123123", "321321321");
            // when
            memberService.updateUserPassword(member, request);

            // then
            assertThat(passwordResolver.isInvalidPasswordOf(member, "321321321")).isFalse();
        }

        @Test
        @DisplayName("기존 비밀번호를 모른다면 비밀번호 업데이트가 불가능하다.")
        void originPasswordInvalidTest() {
            // given
            Member member = memberRepository.findByEmail("test@test.com").get();
            PasswordUpdateRequest request = new PasswordUpdateRequest("111111111", "321321321");
            // when then
            assertThrows(InvalidPasswordException.class, () -> memberService.updateUserPassword(member, request));
        }
    }
}