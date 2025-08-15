package com.gradation.zmnnoory.domain.datarequest.dto.request;

import jakarta.validation.constraints.Size;

public record DataRequestNotesUpdateRequest(
        
        @Size(max = 1000, message = "관리자 메모는 1000자 이하로 입력해주세요.")
        String adminNotes
) {
}