package com.gradation.zmnnoory.domain.game.service;

import com.gradation.zmnnoory.domain.game.dto.request.GameCreateRequest;
import com.gradation.zmnnoory.domain.game.dto.request.GameSearchRequest;
import com.gradation.zmnnoory.domain.game.dto.request.GameUpdateRequest;
import com.gradation.zmnnoory.domain.game.dto.response.GameResponse;
import com.gradation.zmnnoory.domain.game.entity.Game;
import com.gradation.zmnnoory.domain.game.entity.GameUpdater;
import com.gradation.zmnnoory.domain.game.exception.GameNotFoundException;
import com.gradation.zmnnoory.domain.game.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;

    // 1. 게임 등록
    public GameResponse createGame(GameCreateRequest request) {
        Game saved = gameRepository.save(request.toEntity());
        return GameResponse.of(saved);
    }

    // 2. 전체 조회
    public List<GameResponse> getAllGames() {
        return gameRepository.findAll().stream()
                .map(GameResponse::of)
                .collect(Collectors.toList());
    }

    // 3. 단건 조회
    public GameResponse getGameById(Long id) {
        Game game = gameRepository.findById(id)
                .orElseThrow(() -> new GameNotFoundException(id));
        return GameResponse.of(game);
    }

    // 4. 게임 수정
    public GameResponse updateGame(Long id, GameUpdateRequest request) {
        Game game = gameRepository.findById(id)
                .orElseThrow(() -> new GameNotFoundException(id));

        GameUpdater updater = new GameUpdater(
                request.title(),
                request.description(),
                request.explanation(),
                request.difficulty(),
                request.point(),
                request.thumbnail(),
                request.requiredDataType()
        );
        game.update(updater);

        return GameResponse.of(gameRepository.save(game));
    }

    // 5. 게임 삭제
    public void deleteGame(Long id) {
        Game game = gameRepository.findById(id)
                .orElseThrow(() -> new GameNotFoundException(id));
        gameRepository.delete(game);
    }

    // 6. 검색 (난이도 기준)
    public List<GameResponse> searchGames(GameSearchRequest request) {
        List<Game> games = gameRepository.findByDifficulty(request.difficulty());
        return games.stream()
                .map(GameResponse::of)
                .collect(Collectors.toList());
    }
}
