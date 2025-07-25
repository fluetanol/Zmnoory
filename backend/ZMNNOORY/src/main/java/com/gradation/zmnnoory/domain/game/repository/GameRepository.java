package com.gradation.zmnnoory.domain.game.repository;

import com.gradation.zmnnoory.domain.game.entity.Game;
import com.gradation.zmnnoory.domain.game.entity.GameDifficulty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
    
    Optional<Game> findByTitle(String title);
    
    List<Game> findByDifficulty(GameDifficulty difficulty);
}
