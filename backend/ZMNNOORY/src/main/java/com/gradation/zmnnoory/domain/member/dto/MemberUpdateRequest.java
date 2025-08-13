package com.gradation.zmnnoory.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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

        @NotNull(message = "생년월일은 필수 값입니다.")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate birthday,

        @NotNull(message = "선택 약관 동의 여부는 필수 값입니다.")
        Boolean optionalConsent
) {
}
