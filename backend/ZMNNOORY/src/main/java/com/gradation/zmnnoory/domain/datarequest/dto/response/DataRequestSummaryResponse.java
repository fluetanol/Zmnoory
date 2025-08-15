package com.gradation.zmnnoory.domain.datarequest.dto.response;

import com.gradation.zmnnoory.domain.datarequest.entity.DataRequest;
import com.gradation.zmnnoory.domain.datarequest.entity.DataRequestStatus;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record DataRequestSummaryResponse(
        Long id,
        String companyName,
        String contactInfo,
        DataRequestStatus status,
        LocalDateTime requestDate,
        LocalDateTime processedDate
) {
    
    public static DataRequestSummaryResponse of(DataRequest dataRequest) {
        return DataRequestSummaryResponse.builder()
                .id(dataRequest.getId())
                .companyName(dataRequest.getCompanyName())
                .contactInfo(dataRequest.getContactInfo())
                .status(dataRequest.getStatus())
                .requestDate(dataRequest.getRequestDate())
                .processedDate(dataRequest.getProcessedDate())
                .build();
    }
}