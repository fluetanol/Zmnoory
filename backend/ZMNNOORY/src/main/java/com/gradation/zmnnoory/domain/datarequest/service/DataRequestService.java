package com.gradation.zmnnoory.domain.datarequest.service;

import com.gradation.zmnnoory.domain.datarequest.dto.request.DataRequestCreateRequest;
import com.gradation.zmnnoory.domain.datarequest.dto.request.DataRequestNotesUpdateRequest;
import com.gradation.zmnnoory.domain.datarequest.dto.request.DataRequestStatusUpdateRequest;
import com.gradation.zmnnoory.domain.datarequest.dto.response.DataRequestResponse;
import com.gradation.zmnnoory.domain.datarequest.dto.response.DataRequestSummaryResponse;
import com.gradation.zmnnoory.domain.datarequest.entity.DataRequest;
import com.gradation.zmnnoory.domain.datarequest.entity.DataRequestStatus;
import com.gradation.zmnnoory.domain.datarequest.exception.DataRequestNotFoundException;
import com.gradation.zmnnoory.domain.datarequest.exception.InvalidStatusTransitionException;
import com.gradation.zmnnoory.domain.datarequest.repository.DataRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DataRequestService {

    private final DataRequestRepository dataRequestRepository;

    @Transactional
    public DataRequestResponse createDataRequest(DataRequestCreateRequest request) {
        DataRequest dataRequest = DataRequest.builder()
                .companyName(request.companyName())
                .contactInfo(request.contactInfo())
                .dataRequirements(request.dataRequirements())
                .build();

        DataRequest savedDataRequest = dataRequestRepository.save(dataRequest);
        return DataRequestResponse.of(savedDataRequest);
    }

    public List<DataRequestSummaryResponse> getAllDataRequests() {
        return dataRequestRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(DataRequestSummaryResponse::of)
                .toList();
    }

    public Page<DataRequestSummaryResponse> getAllDataRequestsPaged(Pageable pageable) {
        return dataRequestRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(DataRequestSummaryResponse::of);
    }

    public DataRequestResponse getDataRequest(Long id) {
        DataRequest dataRequest = dataRequestRepository.findById(id)
                .orElseThrow(DataRequestNotFoundException::new);
        return DataRequestResponse.of(dataRequest);
    }

    public List<DataRequestSummaryResponse> getDataRequestsByStatus(DataRequestStatus status) {
        return dataRequestRepository.findByStatusOrderByCreatedAtDesc(status)
                .stream()
                .map(DataRequestSummaryResponse::of)
                .toList();
    }

    public List<DataRequestResponse> getMyDataRequests(String contactInfo) {
        return dataRequestRepository.findByContactInfoOrderByCreatedAtDesc(contactInfo)
                .stream()
                .map(DataRequestResponse::of)
                .toList();
    }

    @Transactional
    public DataRequestResponse updateStatus(Long id, DataRequestStatusUpdateRequest request) {
        DataRequest dataRequest = dataRequestRepository.findById(id)
                .orElseThrow(DataRequestNotFoundException::new);

        if (!isValidStatusTransition(dataRequest.getStatus(), request.status())) {
            throw new InvalidStatusTransitionException();
        }

        dataRequest.updateStatus(request.status());
        return DataRequestResponse.of(dataRequest);
    }

    @Transactional
    public DataRequestResponse updateAdminNotes(Long id, DataRequestNotesUpdateRequest request) {
        DataRequest dataRequest = dataRequestRepository.findById(id)
                .orElseThrow(DataRequestNotFoundException::new);

        dataRequest.updateAdminNotes(request.adminNotes());
        return DataRequestResponse.of(dataRequest);
    }

    private boolean isValidStatusTransition(DataRequestStatus currentStatus, DataRequestStatus newStatus) {
        if (currentStatus == newStatus) {
            return true;
        }

        return switch (currentStatus) {
            case PENDING -> newStatus == DataRequestStatus.REVIEWING || 
                           newStatus == DataRequestStatus.REJECTED;
            case REVIEWING -> newStatus == DataRequestStatus.APPROVED || 
                             newStatus == DataRequestStatus.REJECTED ||
                             newStatus == DataRequestStatus.PENDING;
            case APPROVED -> newStatus == DataRequestStatus.IN_PROGRESS;
            case IN_PROGRESS -> newStatus == DataRequestStatus.COMPLETED ||
                               newStatus == DataRequestStatus.APPROVED;
            case COMPLETED -> false; // 완료된 요청은 상태 변경 불가
            case REJECTED -> newStatus == DataRequestStatus.PENDING; // 거절된 요청은 대기로만 변경 가능
        };
    }

    public long getCountByStatus(DataRequestStatus status) {
        return dataRequestRepository.countByStatus(status);
    }
}