package com.gradation.zmnnoory.domain.member.service;

import com.gradation.zmnnoory.domain.member.dto.MemberUpdateRequest;
import com.gradation.zmnnoory.domain.member.dto.response.MemberResponse;
import com.gradation.zmnnoory.domain.member.entity.Gender;
import com.gradation.zmnnoory.domain.member.entity.Member;
import com.gradation.zmnnoory.domain.member.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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

    @BeforeEach
    void setup() {
        Member member = Member.builder()
                .email("test@test.com")
                .password("123123123")
                .gender(Gender.MALE)
                .birthday(LocalDate.of(2000, 1, 1))
                .nickname("tester")
                .build();

        memberRepository.save(member);
    }

    @Test
    @DisplayName("회원 정보 업데이트 테스트")
    void updateMemberTest() {
        // given
        Member member = memberRepository.findByEmail("test@test.com").orElseThrow();
        
        MemberUpdateRequest updateRequest = new MemberUpdateRequest(
                "updated@test.com",
                "newpassword123",
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
                "newpassword123",
                Gender.FEMALE,
                "updatedNickname",
                LocalDate.of(1995, 5, 15)
        );

        // when & then
        assertThrows(EntityNotFoundException.class, () -> {
            memberService.updateUserInfoWith(nonExistentMemberEmail, updateRequest);
        });
    }
}