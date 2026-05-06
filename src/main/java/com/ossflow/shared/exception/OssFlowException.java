package com.ossflow.shared.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Getter
public abstract class OssFlowException extends RuntimeException {

    private final String errorCode;
    private final Map<String, Object> details;

    protected OssFlowException(String errorCode, String message, Map<String, Object> details) {
        super(message);
        this.errorCode = errorCode;
        this.details = details == null ? Map.of() : Map.copyOf(details);
    }

    public abstract HttpStatus getHttpStatus();
}
