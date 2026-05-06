package com.ossflow.shared.validation;

import java.util.Map;

public sealed interface ValidationResult permits ValidationResult.Ok, ValidationResult.Fail {
    record Ok() implements ValidationResult {}
    record Fail(String errorCode, String message, Map<String, Object> details) implements ValidationResult {}
}
