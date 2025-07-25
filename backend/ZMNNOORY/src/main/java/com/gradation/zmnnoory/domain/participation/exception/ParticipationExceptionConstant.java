package com.gradation.zmnnoory.domain.participation.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ParticipationExceptionConstant {

    PARTICIPATION_NOT_FOUND_EXCEPTION("참여 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),;

    private final String message;
    private final HttpStatus status;
    }
