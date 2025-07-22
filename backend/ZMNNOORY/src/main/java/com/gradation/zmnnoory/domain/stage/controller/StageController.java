package com.gradation.zmnnoory.domain.stage.controller;

import com.gradation.zmnnoory.common.dto.BaseResponse;
import com.gradation.zmnnoory.domain.stage.dto.StageRequest;
import com.gradation.zmnnoory.domain.stage.dto.StageResponse;
import com.gradation.zmnnoory.domain.stage.service.StageService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stages")
@Validated
public class StageController {

    private final StageService stageService;

    // GET /api/stages - 모든 스테이지 조회
    @GetMapping
    public BaseResponse<List<StageResponse>> getAllStages(){
        return BaseResponse.<List<StageResponse>>builder()
                .status(HttpStatus.OK)
                .data(stageService.getAllStages())
                .build();
    }

    // GET /api/stages/{id} - 특정 스테이지 조회
    @GetMapping("/{id}")
    public BaseResponse<StageResponse> getStageById(@PathVariable @NotNull @Positive Long id){
        return BaseResponse.<StageResponse>builder()
                .status(HttpStatus.OK)
                .data(stageService.getStageById(id))
                .build();
    }

    // POST /api/stages - 관리자용 스테이지 생성
    @PostMapping
    public BaseResponse<StageResponse> createStage(@Valid @RequestBody StageRequest request) {
        return BaseResponse.<StageResponse>builder()
                .status(HttpStatus.CREATED)
                .data(stageService.createStage(request))
                .build();
    }
}
