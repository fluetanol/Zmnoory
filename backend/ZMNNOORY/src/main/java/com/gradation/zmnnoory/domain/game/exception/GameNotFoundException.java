package com.gradation.zmnnoory.domain.game.exception;

public class GameNotFoundException extends RuntimeException {

    public GameNotFoundException(Long id) {
        super("존재하지 않는 게임입니다. (ID: " + id + ")");
    }
}
