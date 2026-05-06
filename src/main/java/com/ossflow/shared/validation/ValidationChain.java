package com.ossflow.shared.validation;

import java.util.List;

public class ValidationChain<T> {
    private final List<ValidationStep<T>> steps;

    public ValidationChain(List<ValidationStep<T>> steps) {
        this.steps = steps;
    }

    public ValidationResult run(T payload) {
        var ctx = new ValidationContext();
        for (var step : steps) {
            var result = step.validate(payload, ctx);
            if (result instanceof ValidationResult.Fail) return result;
        }
        return new ValidationResult.Ok();
    }
}
