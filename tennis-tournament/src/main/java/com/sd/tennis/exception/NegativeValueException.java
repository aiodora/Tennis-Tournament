package com.sd.tennis.exception;

import java.io.Serial;

public class NegativeValueException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public NegativeValueException(String message) {
        super(message);
    }

    public NegativeValueException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s cannot be negative with %s: '%s'", resourceName, fieldName, fieldValue));
    }
}
