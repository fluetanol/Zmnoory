package com.gradation.zmnnoory.domain.stage.controller;

import com.gradation.zmnnoory.domain.stage.dto.StageRequest;
import com.gradation.zmnnoory.domain.stage.dto.StageResponse;
import com.gradation.zmnnoory.domain.stage.service.StageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/stages")
public class StageController {

    private final StageService stageService;

    // GET /api/stages - 모든 스테이지 조회
    @GetMapping
    public List<StageResponse> getAllStages() {
        return stageService.getAllStages();
    }

    // GET /api/stages/{id} - 특정 스테이지 조회
    @GetMapping("/{id}")
    public StageResponse getStageById(@PathVariable Long id) {
        return stageService.getStageById(id);
    }

    // POST /api/stages - 관리자용 스테이지 생성
    @PostMapping
    public StageResponse createStage(@RequestBody StageRequest request) {
        return stageService.createStage(request);
    }
}
