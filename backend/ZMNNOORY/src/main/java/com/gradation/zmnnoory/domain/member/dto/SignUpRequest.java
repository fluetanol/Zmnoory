package com.gradation.zmnnoory.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SignUpRequest {

    private final String email;
    private final String password;
    private final String gender;
}
