package com.gradation.zmnnoory.domain.member.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SignUpRequest {


    @NotBlank(message = "이메일은 필수 값입니다.")
    @Email(message = "이메일 형식이 아닙니다.")
    private final String email;

    @NotBlank(message = "비밀번호는 필수 값입니다.")
    private final String password;

    @NotBlank(message = "성별은 필수 값입니다.")
    private final String gender;
}
