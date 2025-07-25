package com.gradation.zmnnoory.domain.member.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gradation.zmnnoory.domain.member.entity.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Schema(description = "회원가입 요청")
public record SignUpRequest(
        @Schema(description = "사용자 이메일 주소", example = "user@example.com")
        @NotBlank(message = "이메일은 필수 값입니다.")
        @Email(message = "이메일 형식이 아닙니다.") String email,

        @Schema(description = "사용자 비밀번호", example = "password1234")
        @NotBlank(message = "비밀번호는 필수 값입니다.")
        String password,

        @Schema(description = "성별 (예: MALE, FEMALE)", example = "MALE")
        @NotNull(message = "성별은 필수 값입니다.")
        Gender gender,

        @Schema(description = "사용자 닉네임", example = "johnny")
        @NotBlank(message = "닉네임은 필수값입니다.")
        String nickname,

        @Schema(description = "생년월일 (yyyy-MM-dd 형식)", example = "1990-01-01")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        LocalDate birthday,

        @Schema(description = "추천인 이메일", example = "recommender@test.com")
        @Email(message = "추천인은 이메일이어야 합니다.")
        String recommenderEmail
) {
}