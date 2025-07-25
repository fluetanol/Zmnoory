package com.gradation.zmnnoory.domain.game.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum GameExceptionConstants {

    GAME_NOT_FOUND_EXCEPTION("존재하지 않는 게임입니다.", HttpStatus.NOT_FOUND),
    ;

    private final String message;
    private final HttpStatus status;
}
