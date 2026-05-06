package com.ossflow.shared.exception;

import java.util.Map;

public class ReferenceInUseException extends ConflictException {
    public ReferenceInUseException(String errorCode, String message, Map<String, Object> details) {
        super(errorCode, message, details);
    }
}
