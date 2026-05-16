package com.ossflow.shared.exception;

import org.springframework.http.HttpStatus;

import java.util.Map;

public class ForbiddenException extends OssFlowException {
    public ForbiddenException(String errorCode, String message, Map<String, Object> details) {
        super(errorCode, message, details);
    }
    public ForbiddenException(String errorCode, String message) {
        this(errorCode, message, Map.of());
    }
    @Override public HttpStatus getHttpStatus() { return HttpStatus.FORBIDDEN; }
}
