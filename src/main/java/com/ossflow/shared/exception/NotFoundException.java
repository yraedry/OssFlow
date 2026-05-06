package com.ossflow.shared.exception;

import org.springframework.http.HttpStatus;

import java.util.Map;

public class NotFoundException extends OssFlowException {
    public NotFoundException(String errorCode, String message, Map<String, Object> details) {
        super(errorCode, message, details);
    }
    public NotFoundException(String errorCode, String message) {
        this(errorCode, message, Map.of());
    }
    @Override public HttpStatus getHttpStatus() { return HttpStatus.NOT_FOUND; }
}
