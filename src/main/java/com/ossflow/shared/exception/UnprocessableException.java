package com.ossflow.shared.exception;

import org.springframework.http.HttpStatus;

import java.util.Map;

public class UnprocessableException extends OssFlowException {
    public UnprocessableException(String errorCode, String message, Map<String, Object> details) {
        super(errorCode, message, details);
    }
    public UnprocessableException(String errorCode, String message) {
        this(errorCode, message, Map.of());
    }
    @Override public HttpStatus getHttpStatus() { return HttpStatus.UNPROCESSABLE_ENTITY; }
}
