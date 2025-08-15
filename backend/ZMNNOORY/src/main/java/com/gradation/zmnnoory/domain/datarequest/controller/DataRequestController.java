package com.gradation.zmnnoory.domain.datarequest.controller;

import com.gradation.zmnnoory.common.dto.BaseResponse;
import com.gradation.zmnnoory.domain.datarequest.dto.request.DataRequestCreateRequest;
import com.gradation.zmnnoory.domain.datarequest.dto.request.DataRequestNotesUpdateRequest;
import com.gradation.zmnnoory.domain.datarequest.dto.request.DataRequestStatusUpdateRequest;
import com.gradation.zmnnoory.domain.datarequest.dto.response.DataRequestResponse;
import com.gradation.zmnnoory.domain.datarequest.dto.response.DataRequestSummaryResponse;
import com.gradation.zmnnoory.domain.datarequest.entity.DataRequestStatus;
import com.gradation.zmnnoory.domain.datarequest.service.DataRequestService;
import com.gradation.zmnnoory.domain.member.annotation.AdminOnly;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@Tag(name = "데이터 요청 API", description = "데이터 요청 관리 API")
public class DataRequestController {

    private final DataRequestService dataRequestService;

    @Operation(
            summary = "데이터 요청 제출",
            description = """
                    사용자가 데이터 요청을 제출합니다.
                    - 기업명, 연락처, 필요 데이터 정보를 입력합니다.
                    - 제출된 요청은 관리자가 검토합니다.
                    """
    )
    @PostMapping("/api/data-requests")
    public BaseResponse<DataRequestResponse> createDataRequest(
            @Valid @RequestBody DataRequestCreateRequest request) {

        DataRequestResponse response = dataRequestService.createDataRequest(request);

        return BaseResponse.<DataRequestResponse>builder()
                .status(HttpStatus.CREATED)
                .data(response)
                .build();
    }

    @Operation(
            summary = "내 데이터 요청 목록 조회",
            description = """
                    특정 연락처로 제출된 데이터 요청 목록을 조회합니다.
                    - 연락처(이메일)를 기준으로 본인의 요청만 조회할 수 있습니다.
                    """
    )
    @GetMapping("/api/data-requests/my")
    public BaseResponse<List<DataRequestSummaryResponse>> getMyDataRequests(
            @Parameter(description = "연락처(이메일)", required = true)
            @RequestParam String contactInfo) {

        List<DataRequestSummaryResponse> response = dataRequestService.getMyDataRequests(contactInfo);

        return BaseResponse.<List<DataRequestSummaryResponse>>builder()
                .status(HttpStatus.OK)
                .data(response)
                .build();
    }

    @Operation(
            summary = "전체 데이터 요청 목록 조회",
            description = """
                    관리자가 시스템에 등록된 모든 데이터 요청을 조회합니다.
                    - 페이징을 지원합니다.
                    """
    )
    @GetMapping("/api/admin/data-requests")
    @AdminOnly
    public BaseResponse<List<DataRequestSummaryResponse>> getAllDataRequests(
            @Parameter(description = "특정 상태의 요청만 조회 (선택사항)")
            @RequestParam(required = false) DataRequestStatus status) {

        List<DataRequestSummaryResponse> response = status != null 
                ? dataRequestService.getDataRequestsByStatus(status)
                : dataRequestService.getAllDataRequests();

        return BaseResponse.<List<DataRequestSummaryResponse>>builder()
                .status(HttpStatus.OK)
                .data(response)
                .build();
    }

    @Operation(
            summary = "전체 데이터 요청 목록 조회 (페이징)",
            description = """
                    관리자가 시스템에 등록된 모든 데이터 요청을 페이징으로 조회합니다.
                    """
    )
    @GetMapping("/api/admin/data-requests/paged")
    @AdminOnly
    public BaseResponse<Page<DataRequestSummaryResponse>> getAllDataRequestsPaged(
            @PageableDefault(size = 20) Pageable pageable) {

        Page<DataRequestSummaryResponse> response = dataRequestService.getAllDataRequestsPaged(pageable);

        return BaseResponse.<Page<DataRequestSummaryResponse>>builder()
                .status(HttpStatus.OK)
                .data(response)
                .build();
    }

    @Operation(
            summary = "데이터 요청 상세 조회",
            description = """
                    특정 데이터 요청의 상세 정보를 조회합니다.
                    """
    )
    @GetMapping("/api/admin/data-requests/{id}")
    @AdminOnly
    public BaseResponse<DataRequestResponse> getDataRequest(@PathVariable Long id) {

        DataRequestResponse response = dataRequestService.getDataRequest(id);

        return BaseResponse.<DataRequestResponse>builder()
                .status(HttpStatus.OK)
                .data(response)
                .build();
    }

    @Operation(
            summary = "데이터 요청 상태 변경",
            description = """
                    관리자가 데이터 요청의 상태를 변경합니다.
                    - 유효한 상태 전환만 허용됩니다.
                    """
    )
    @PatchMapping("/api/admin/data-requests/{id}/status")
    @AdminOnly
    public BaseResponse<DataRequestResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody DataRequestStatusUpdateRequest request) {

        DataRequestResponse response = dataRequestService.updateStatus(id, request);

        return BaseResponse.<DataRequestResponse>builder()
                .status(HttpStatus.OK)
                .data(response)
                .build();
    }

    @Operation(
            summary = "관리자 메모 추가/수정",
            description = """
                    관리자가 데이터 요청에 메모를 추가하거나 수정합니다.
                    """
    )
    @PatchMapping("/api/admin/data-requests/{id}/notes")
    @AdminOnly
    public BaseResponse<DataRequestResponse> updateAdminNotes(
            @PathVariable Long id,
            @Valid @RequestBody DataRequestNotesUpdateRequest request) {

        DataRequestResponse response = dataRequestService.updateAdminNotes(id, request);

        return BaseResponse.<DataRequestResponse>builder()
                .status(HttpStatus.OK)
                .data(response)
                .build();
    }
}