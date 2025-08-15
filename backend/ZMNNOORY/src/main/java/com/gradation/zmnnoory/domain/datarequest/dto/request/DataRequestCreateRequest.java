package com.gradation.zmnnoory.domain.datarequest.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DataRequestCreateRequest(
        
        @NotBlank(message = "기업명을 입력해주세요.")
        @Size(max = 255, message = "기업명은 255자 이하로 입력해주세요.")
        String companyName,
        
        @NotBlank(message = "연락처를 입력해주세요.")
        @Email(message = "올바른 이메일 형식을 입력해주세요.")
        String contactInfo,
        
        @NotBlank(message = "필요한 데이터 정보를 입력해주세요.")
        @Size(max = 1000, message = "데이터 요구사항은 1000자 이하로 입력해주세요.")
        String dataRequirements
) {
}