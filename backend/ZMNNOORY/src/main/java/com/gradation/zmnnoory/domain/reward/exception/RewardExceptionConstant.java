package com.gradation.zmnnoory.domain.reward.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum RewardExceptionConstant {

    DUPLICATED_REWARD_REQUEST_EXCEPTION("이미 해당 게임에 대한 보상을 지급받았습니다.", HttpStatus.BAD_REQUEST),
    REWARD_NOT_FOUND_EXCEPTION("리워드 로그를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    ;

    private final String message;
    private final HttpStatus status;
}
