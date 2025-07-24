package com.gradation.zmnnoory.domain.game.controller;

import com.gradation.zmnnoory.common.dto.BaseResponse;
import com.gradation.zmnnoory.domain.game.dto.*;
import com.gradation.zmnnoory.domain.game.service.GameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "게임 API", description = "게임 등록, 조회, 수정, 삭제 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/games")
public class GameController {

    private final GameService gameService;

    // 1. 게임 등록
    @Operation(summary = "게임 등록")
    @PostMapping
    public BaseResponse<GameResponse> createGame(@Valid @RequestBody GameCreateRequest request) {
        return BaseResponse.<GameResponse>builder()
                .status(HttpStatus.CREATED)
                .data(gameService.createGame(request))
                .build();
    }

    // 2. 전체 게임 조회
    @Operation(summary = "전체 게임 목록 조회")
    @GetMapping
    public BaseResponse<List<GameResponse>> getAllGames() {
        return BaseResponse.<List<GameResponse>>builder()
                .status(HttpStatus.OK)
                .data(gameService.getAllGames())
                .build();
    }

    // 3. 게임 상세 조회
    @Operation(summary = "게임 단건 조회")
    @GetMapping("/{id}")
    public BaseResponse<GameResponse> getGameById(@PathVariable Long id) {
        return BaseResponse.<GameResponse>builder()
                .status(HttpStatus.OK)
                .data(gameService.getGameById(id))
                .build();
    }

    // 4. 게임 수정
    @Operation(summary = "게임 수정")
    @PutMapping("/{id}")
    public BaseResponse<GameResponse> updateGame(@PathVariable Long id,
                                                 @Valid @RequestBody GameUpdateRequest request) {
        return BaseResponse.<GameResponse>builder()
                .status(HttpStatus.OK)
                .data(gameService.updateGame(id, request))
                .build();
    }

    // 5. 게임 삭제
    @Operation(summary = "게임 삭제")
    @DeleteMapping("/{id}")
    public BaseResponse<Void> deleteGame(@PathVariable Long id) {
        gameService.deleteGame(id);
        return BaseResponse.<Void>builder()
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    // 6. 난이도 기준 검색
    @Operation(summary = "게임 난이도 검색")
    @GetMapping("/search")
    public BaseResponse<List<GameResponse>> searchGames(@ModelAttribute GameSearchRequest request) {
        return BaseResponse.<List<GameResponse>>builder()
                .status(HttpStatus.OK)
                .data(gameService.searchGames(request))
                .build();
    }
}
