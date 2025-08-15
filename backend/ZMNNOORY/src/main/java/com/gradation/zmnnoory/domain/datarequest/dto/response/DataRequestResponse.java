package com.gradation.zmnnoory.domain.datarequest.dto.response;

import com.gradation.zmnnoory.domain.datarequest.entity.DataRequest;
import com.gradation.zmnnoory.domain.datarequest.entity.DataRequestStatus;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record DataRequestResponse(
        Long id,
        String companyName,
        String contactInfo,
        String dataRequirements,
        DataRequestStatus status,
        LocalDateTime requestDate,
        LocalDateTime processedDate,
        String adminNotes,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    
    public static DataRequestResponse of(DataRequest dataRequest) {
        return DataRequestResponse.builder()
                .id(dataRequest.getId())
                .companyName(dataRequest.getCompanyName())
                .contactInfo(dataRequest.getContactInfo())
                .dataRequirements(dataRequest.getDataRequirements())
                .status(dataRequest.getStatus())
                .requestDate(dataRequest.getRequestDate())
                .processedDate(dataRequest.getProcessedDate())
                .adminNotes(dataRequest.getAdminNotes())
                .createdAt(dataRequest.getCreatedAt())
                .updatedAt(dataRequest.getUpdatedAt())
                .build();
    }
}