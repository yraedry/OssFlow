package com.ossflow.shared.validation;

public interface ValidationStep<T> {
    ValidationResult validate(T payload, ValidationContext ctx);
}
