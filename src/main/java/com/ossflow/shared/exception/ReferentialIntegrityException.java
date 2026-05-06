package com.ossflow.shared.exception;

import java.util.Map;

public class ReferentialIntegrityException extends UnprocessableException {
    public ReferentialIntegrityException(String errorCode, String message, Map<String, Object> details) {
        super(errorCode, message, details);
    }
}
