package com.ossflow.shared.exception;

import java.util.Map;

public class InvalidStateTransitionException extends ConflictException {
    public InvalidStateTransitionException(String errorCode, String message, Map<String, Object> details) {
        super(errorCode, message, details);
    }
}
