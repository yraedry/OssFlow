package com.ossflow.shared.exception;

import java.util.Map;

public class SemanticValidationException extends UnprocessableException {
    public SemanticValidationException(String errorCode, String message, Map<String, Object> details) {
        super(errorCode, message, details);
    }
}
