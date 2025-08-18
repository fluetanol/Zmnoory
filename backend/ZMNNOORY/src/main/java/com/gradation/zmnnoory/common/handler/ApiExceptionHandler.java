package com.gradation.zmnnoory.common.handler;

import com.gradation.zmnnoory.common.dto.BaseResponse;
import com.gradation.zmnnoory.common.exception.BaseException;
import com.gradation.zmnnoory.common.exception.ValidException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public BaseResponse<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        ValidException validException = new ValidException();

        for (ObjectError error : e.getBindingResult().getAllErrors()) {
            if (error instanceof FieldError fieldError) {
                validException.addValidation(fieldError.getField(), fieldError.getDefaultMessage());
            } else {
                validException.addValidation(error.getObjectName(), error.getDefaultMessage());
            }
        }

        return BaseResponse.fail(validException.getValidations(), validException.getStatus());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public BaseResponse<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        Throwable cause = e.getCause();
        if (cause instanceof BaseException baseException) {
            return BaseResponse.fail(
                    baseException.getValidations(),
                    baseException.getMessage(),
                    baseException.getStatus()
            );
        }
        return BaseResponse.fail(null, e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BaseException.class)
    public BaseResponse<?> handleBaseException(BaseException e) {
        return BaseResponse.fail(e.getValidations(), e.getMessage(), e.getStatus());
    }
    
    @ExceptionHandler(AuthorizationDeniedException.class)
    public BaseResponse<?> handleAuthorizationDeniedException(AuthorizationDeniedException e) {
        return BaseResponse.fail(null, e.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(Exception.class)
    public BaseResponse<?> handleException(Exception e) {
        return BaseResponse.fail(null, e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
