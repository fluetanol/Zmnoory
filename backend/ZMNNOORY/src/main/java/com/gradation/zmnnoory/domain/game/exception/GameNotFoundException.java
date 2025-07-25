package com.gradation.zmnnoory.domain.game.exception;

import com.gradation.zmnnoory.common.exception.BaseException;
import org.springframework.http.HttpStatus;

import static com.gradation.zmnnoory.domain.game.exception.GameExceptionConstants.GAME_NOT_FOUND_EXCEPTION;

public class GameNotFoundException extends BaseException {

    public GameNotFoundException(Long gameId) {
        super(GAME_NOT_FOUND_EXCEPTION.getMessage() + " " + gameId);
    }

    public GameNotFoundException(String gameTitle) {
        super(GAME_NOT_FOUND_EXCEPTION.getMessage() + " " + gameTitle);
    }

    @Override
    public HttpStatus getStatus() {
        return GAME_NOT_FOUND_EXCEPTION.getStatus();
    }
}
