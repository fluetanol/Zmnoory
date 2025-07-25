package com.gradation.zmnnoory.domain.participation.exception;

import com.gradation.zmnnoory.common.exception.BaseException;
import org.springframework.http.HttpStatus;

import static com.gradation.zmnnoory.domain.participation.exception.ParticipationExceptionConstant.PARTICIPATION_NOT_FOUND_EXCEPTION;

public class ParticipationNotFoundException extends BaseException {

    public ParticipationNotFoundException() {
        super(PARTICIPATION_NOT_FOUND_EXCEPTION.getMessage());
    }

    @Override
    public HttpStatus getStatus() {
        return PARTICIPATION_NOT_FOUND_EXCEPTION.getStatus();
    }
}
