package com.ossflow.shared.validation;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ValidationChainTest {

    @Test
    void should_return_ok_when_no_steps_defined() {
        var chain = new ValidationChain<String>(List.of());

        var result = chain.run("any");

        assertThat(result).isInstanceOf(ValidationResult.Ok.class);
    }

    @Test
    void should_return_ok_when_all_steps_pass() {
        ValidationStep<String> alwaysOk = (payload, ctx) -> new ValidationResult.Ok();
        var chain = new ValidationChain<>(List.of(alwaysOk, alwaysOk));

        var result = chain.run("payload");

        assertThat(result).isInstanceOf(ValidationResult.Ok.class);
    }

    @Test
    void should_return_fail_when_first_step_fails() {
        ValidationStep<String> failStep = (payload, ctx) ->
                new ValidationResult.Fail("CODE_A", "fallo en paso 1", Map.of("field", "x"));
        ValidationStep<String> neverReached = (payload, ctx) -> new ValidationResult.Ok();
        var chain = new ValidationChain<>(List.of(failStep, neverReached));

        var result = chain.run("payload");

        assertThat(result).isInstanceOf(ValidationResult.Fail.class);
        assertThat(((ValidationResult.Fail) result).errorCode()).isEqualTo("CODE_A");
    }

    @Test
    void should_stop_at_first_failing_step_and_not_execute_subsequent_steps() {
        var executionTracker = new java.util.ArrayList<String>();
        ValidationStep<String> failStep = (payload, ctx) -> {
            executionTracker.add("step1");
            return new ValidationResult.Fail("FAIL", "fallo", Map.of());
        };
        ValidationStep<String> shouldNotRun = (payload, ctx) -> {
            executionTracker.add("step2");
            return new ValidationResult.Ok();
        };
        var chain = new ValidationChain<>(List.of(failStep, shouldNotRun));

        chain.run("test");

        assertThat(executionTracker).containsExactly("step1");
    }

    @Test
    void should_pass_context_between_steps() {
        ValidationStep<String> producerStep = (payload, ctx) -> {
            ctx.put("computed", "value-from-step1");
            return new ValidationResult.Ok();
        };
        var capturedValues = new java.util.ArrayList<String>();
        ValidationStep<String> consumerStep = (payload, ctx) -> {
            capturedValues.add(ctx.get("computed"));
            return new ValidationResult.Ok();
        };
        var chain = new ValidationChain<>(List.of(producerStep, consumerStep));

        chain.run("payload");

        assertThat(capturedValues).containsExactly("value-from-step1");
    }
}
