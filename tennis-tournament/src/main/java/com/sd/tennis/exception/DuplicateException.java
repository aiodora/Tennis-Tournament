package com.sd.tennis.exception;

import java.io.Serial;

public class DuplicateException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public DuplicateException(String message) {
        super(message);
    }

    public DuplicateException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s already exists with %s: '%s'", resourceName, fieldName, fieldValue));
    }
}
