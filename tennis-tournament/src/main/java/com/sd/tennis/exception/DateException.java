package com.sd.tennis.exception;

import java.io.Serial;

public class DateException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public DateException(String message) {
        super(message);
    }

    public DateException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s: '%s'", resourceName, fieldName, fieldValue));
    }
}
