package com.ossflow.shared.exception;

import java.util.Map;

public class DuplicateNameException extends ConflictException {
    public DuplicateNameException(String errorCode, String message, Map<String, Object> details) {
        super(errorCode, message, details);
    }
}
