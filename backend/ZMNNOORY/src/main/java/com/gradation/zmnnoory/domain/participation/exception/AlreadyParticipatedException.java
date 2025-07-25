package com.gradation.zmnnoory.domain.participation.exception;

import com.gradation.zmnnoory.common.exception.BaseException;
import org.springframework.http.HttpStatus;

import static com.gradation.zmnnoory.domain.participation.exception.ParticipationExceptionConstant.ALREADY_PARTICIPATED_EXCEPTION;


public class AlreadyParticipatedException extends BaseException {

    public AlreadyParticipatedException() { super(ALREADY_PARTICIPATED_EXCEPTION.getMessage());}

    @Override
    public HttpStatus getStatus() { return ALREADY_PARTICIPATED_EXCEPTION.getStatus();}

}
