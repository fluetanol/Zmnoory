package com.gradation.zmnnoory.domain.member.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PasswordUpdateRequest {

    @NotBlank(message = "비밀번호는 필수 값입니다.")
    private final String originPassword;

    @NotBlank(message = "새로운 비밀번호는 필수 값입니다.")
    private final String updatedPassword;
}
