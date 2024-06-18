package ru.omon4412.minibank.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ResultTest {
    @Test
    public void testSuccessResult() {
        Result<String> successResult = new Result.Success<>("Operation succeeded");

        assertThat(successResult.isSuccess()).isTrue();
        assertThat(successResult.isFailure()).isFalse();
        assertThat(successResult.getOrNull()).isEqualTo("Operation succeeded");
        assertThat(successResult.exceptionOrNull()).isNull();
    }

    @Test
    public void testFailureResult() {
        Exception exception = new Exception("Operation failed");
        Result<String> failureResult = new Result.Failure<>(exception);

        assertThat(failureResult.isSuccess()).isFalse();
        assertThat(failureResult.isFailure()).isTrue();
        assertThat(failureResult.getOrNull()).isNull();
        assertThat(failureResult.exceptionOrNull()).isEqualTo(exception);
    }
}