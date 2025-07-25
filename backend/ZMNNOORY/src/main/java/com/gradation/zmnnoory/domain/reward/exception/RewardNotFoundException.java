package com.gradation.zmnnoory.domain.reward.exception;

import com.gradation.zmnnoory.common.exception.BaseException;
import org.springframework.http.HttpStatus;

import static com.gradation.zmnnoory.domain.reward.exception.RewardExceptionConstant.REWARD_NOT_FOUND_EXCEPTION;

public class RewardNotFoundException extends BaseException {

    public RewardNotFoundException() {
        super(REWARD_NOT_FOUND_EXCEPTION.getMessage());
    }

    @Override
    public HttpStatus getStatus() {
        return REWARD_NOT_FOUND_EXCEPTION.getStatus();
    }
}
