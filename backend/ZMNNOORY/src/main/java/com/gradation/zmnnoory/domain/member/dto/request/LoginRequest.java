package com.gradation.zmnnoory.domain.member.dto.request;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginRequest {

    @Email(message = "이메일 형식이 아닙니다.")
    private final String email;
    private final String password;


}
