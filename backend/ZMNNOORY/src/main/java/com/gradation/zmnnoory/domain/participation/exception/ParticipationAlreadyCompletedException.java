package com.gradation.zmnnoory.domain.participation.exception;

import com.gradation.zmnnoory.common.exception.BaseException;
import org.springframework.http.HttpStatus;

import static com.gradation.zmnnoory.domain.participation.exception.ParticipationExceptionConstant.PARTICIPATION_ALREADY_COMPLETED;

public class ParticipationAlreadyCompletedException extends BaseException {

    public ParticipationAlreadyCompletedException() {
        super(PARTICIPATION_ALREADY_COMPLETED.getMessage());
    }

    @Override
    public HttpStatus getStatus() {
        return PARTICIPATION_ALREADY_COMPLETED.getStatus();
    }

}