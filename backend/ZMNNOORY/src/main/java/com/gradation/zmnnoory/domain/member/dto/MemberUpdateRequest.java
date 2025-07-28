package com.gradation.zmnnoory.domain.member.dto;

import com.gradation.zmnnoory.domain.member.entity.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;


public record MemberUpdateRequest(

        @Email(message = "이메일 형식이어야합니다.")
        @NotBlank(message = "이메일은 필수 값입니다.")
        String email,

        @NotNull(message = "성별은 필수 값입니다.")
        Gender gender,

        @NotBlank(message = "닉네임은 필수 값입니다.")
        String nickname,


        @NotBlank(message = "생년월일은 필수 값입니다.")
        LocalDate birthday
) {
}
