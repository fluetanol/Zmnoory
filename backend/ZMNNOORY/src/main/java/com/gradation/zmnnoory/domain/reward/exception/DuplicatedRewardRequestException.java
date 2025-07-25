package com.gradation.zmnnoory.domain.reward.exception;

import com.gradation.zmnnoory.common.exception.BaseException;
import org.springframework.http.HttpStatus;

import static com.gradation.zmnnoory.domain.reward.exception.RewardExceptionConstant.DUPLICATED_REWARD_REQUEST_EXCEPTION;

public class DuplicatedRewardRequestException extends BaseException {

    public DuplicatedRewardRequestException() {
        super(DUPLICATED_REWARD_REQUEST_EXCEPTION.getMessage());
    }

    @Override
    public HttpStatus getStatus() {
        return DUPLICATED_REWARD_REQUEST_EXCEPTION.getStatus();
    }
}
