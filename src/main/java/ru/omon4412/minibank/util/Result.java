package ru.omon4412.minibank.util;

import jakarta.annotation.Nonnull;
import lombok.Getter;

public abstract class Result<T> {

    private Result() {
    }

    public boolean isSuccess() {
        return this instanceof Success;
    }

    public boolean isFailure() {
        return this instanceof Failure;
    }

    public T getOrNull() {
        if (this instanceof Success) {
            return ((Success<T>) this).getData();
        } else {
            return null;
        }
    }

    public Throwable exceptionOrNull() {
        if (this instanceof Failure) {
            return ((Failure<T>) this).getError();
        } else {
            return null;
        }
    }

    @Getter
    public static final class Success<T> extends Result<T> {
        private final T data;

        public Success(T data) {
            this.data = data;
        }

    }

    @Getter
    public static final class Failure<T> extends Result<T> {
        private final Throwable error;

        public Failure(@Nonnull Throwable error) {
            this.error = error;
        }
    }
}

