package com.gradation.zmnnoory.common.exception;

import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

public abstract class BaseException extends RuntimeException {

    private final Map<String, String> validations = new HashMap<>();

    public BaseException(String message) {
        super(message);
    }

    public abstract HttpStatus getStatus();

    public void addValidation(String field, String message) {
        validations.put(field, message);
    }
}
