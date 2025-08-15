package com.gradation.zmnnoory.domain.datarequest.dto.request;

import com.gradation.zmnnoory.domain.datarequest.entity.DataRequestStatus;
import jakarta.validation.constraints.NotNull;

public record DataRequestStatusUpdateRequest(
        
        @NotNull(message = "상태를 선택해주세요.")
        DataRequestStatus status
) {
}