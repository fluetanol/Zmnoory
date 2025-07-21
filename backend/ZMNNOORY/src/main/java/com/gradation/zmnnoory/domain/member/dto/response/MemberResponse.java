package com.gradation.zmnnoory.domain.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberResponse {

    private final String email;
    private final String gender;
}
