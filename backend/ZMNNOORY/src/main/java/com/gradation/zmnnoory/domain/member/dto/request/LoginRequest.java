package com.gradation.zmnnoory.domain.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "로그인 요청")
public record LoginRequest(

        @Schema(description = "사용자 이메일", example = "user@user.com")
        @Email(message = "이메일 형식이 아닙니다.")
        String email,

        @Schema(description = "사용자 비밀번호", example = "password1234")
        @NotBlank(message = "비밀번호는 필수 값입니다.")
        String password) {

}
