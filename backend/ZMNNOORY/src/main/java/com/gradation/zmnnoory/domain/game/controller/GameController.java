package com.gradation.zmnnoory.domain.game.controller;

import com.gradation.zmnnoory.common.dto.BaseResponse;
import com.gradation.zmnnoory.domain.game.dto.*;
import com.gradation.zmnnoory.domain.game.service.GameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "게임 API", description = "게임 등록, 조회, 수정, 삭제 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/games")
@Validated
public class GameController {

    private final GameService gameService;

    // 1. 게임 등록
    @Operation(
            summary = "게임 등록",
            description =
            """
            새로운 게임 정보를 등록합니다.
            - 제목, 설명, 난이도, 리워드 포인트, 썸네일 URL, 요구 데이터 형식 등을 입력받습니다.
            - 입력값은 유효성 검증을 거치며, 유효하지 않을 경우 400 에러를 반환합니다.
            """
    )
    @PostMapping
    public BaseResponse<GameResponse> createGame(
            @Valid @RequestBody GameCreateRequest request) {

        return BaseResponse.<GameResponse>builder()
                .status(HttpStatus.CREATED)
                .data(gameService.createGame(request))
                .build();
    }

    // 2. 전체 게임 조회
    @Operation(
            summary = "전체 게임 목록 조회",
            description =
            """
            등록된 모든 게임 정보를 리스트 형태로 반환합니다.
            - 정렬 또는 필터링 없이 전체 게임 데이터를 반환합니다.
            - 각 게임은 요약 정보(DTO)로 제공됩니다.
            """
    )
    @GetMapping
    public BaseResponse<List<GameResponse>> getAllGames() {

        return BaseResponse.<List<GameResponse>>builder()
                .status(HttpStatus.OK)
                .data(gameService.getAllGames())
                .build();
    }

    // 3. 게임 상세 조회
    @Operation(
            summary = "게임 단건 조회",
            description =
            """
            특정 ID에 해당하는 게임 정보를 상세히 조회합니다.
            - 존재하지 않는 ID를 조회하면 404 에러가 반환됩니다.
            """
    )
    @GetMapping("/{id}")
    public BaseResponse<GameResponse> getGameById(
            @PathVariable Long id) {

        return BaseResponse.<GameResponse>builder()
                .status(HttpStatus.OK)
                .data(gameService.getGameById(id))
                .build();
    }

    // 4. 게임 수정
    @Operation(
            summary = "게임 수정",
            description =
            """
            기존 게임 정보를 수정합니다.
            - 게임 ID를 경로 파라미터로 받아 해당 게임을 수정합니다.
            - 요청 본문으로 수정할 게임 정보를 전달하며, 유효성 검사가 수행됩니다.
            - 존재하지 않는 게임의 경우 404 에러가 반환됩니다.
            """
    )
    @PutMapping("/{id}")
    public BaseResponse<GameResponse> updateGame(
            @PathVariable Long id, @Valid @RequestBody GameUpdateRequest request) {

        return BaseResponse.<GameResponse>builder()
                .status(HttpStatus.OK)
                .data(gameService.updateGame(id, request))
                .build();
    }

    // 5. 게임 삭제
    @Operation(
            summary = "게임 삭제",
            description =
            """
            특정 ID에 해당하는 게임 정보를 삭제합니다.
            - 삭제 후 204 No Content 상태 코드가 반환됩니다.
            - 존재하지 않는 게임을 삭제하려 할 경우 404 에러가 발생합니다.
            """
    )
    @DeleteMapping("/{id}")
    public BaseResponse<Void> deleteGame(
            @PathVariable Long id) {
        gameService.deleteGame(id);

        return BaseResponse.<Void>builder()
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    // 6. 난이도 기준 검색
    @Operation(
            summary = "게임 난이도 검색",
            description =
            """
            쿼리 파라미터를 통해 게임 난이도를 전달하여 해당 난이도의 게임 리스트를 조회합니다.
            - 사용 가능한 값: EASY, MEDIUM, HARD, VERY_HARD
            - 예시: /api/games/search?difficulty=EASY
            """
    )
    @GetMapping("/search")
    public BaseResponse<List<GameResponse>> searchGames(
            @ModelAttribute GameSearchRequest request) {

        return BaseResponse.<List<GameResponse>>builder()
                .status(HttpStatus.OK)
                .data(gameService.searchGames(request))
                .build();
    }
}
