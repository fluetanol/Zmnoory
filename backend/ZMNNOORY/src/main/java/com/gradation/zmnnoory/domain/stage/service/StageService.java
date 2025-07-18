package com.gradation.zmnnoory.domain.stage.service;

import com.gradation.zmnnoory.domain.stage.dto.StageRequest;
import com.gradation.zmnnoory.domain.stage.dto.StageResponse;
import com.gradation.zmnnoory.domain.stage.entity.Stage;
import com.gradation.zmnnoory.domain.stage.repository.StageRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class StageService {

    private final StageRepository stageRepository;
    // 타임 변수명

    // 모든 스테이지 리스트 조회
    public List<StageResponse> getAllStages() {
        // 1. 코드가 길지만 쉬운방법
//        List<Stage> stages = stageRepository.findAll();
//        List<StageResponse> stageResponses = new ArrayList<>();
//
//        for (Stage stage : stages) {
//            stageResponses.add(StageResponse.from(stage));
//        }
//        return stageResponses;

        // 2. 코드가 짧지만 어려운 방법
        return stageRepository.findAll().stream()
                .map(StageResponse::from)
                .toList();
    }

    // 특정 스테이지 조회
    public StageResponse getStageById(Long id) {
        Stage stage = stageRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("해당 스테이지가 없습니다."));
        return StageResponse.from(stage);
    }

    // 새 스테이지 생성 (관리자용)
    public StageResponse createStage(StageRequest request) {
        Stage stage = request.toEntity();
        Stage savedStage = stageRepository.save(stage);
        return StageResponse.from(savedStage);
    }
}
