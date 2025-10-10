/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth;

import jakarta.annotation.Nullable;
import java.util.function.Function;
import lombok.NonNull;

/**
 * A wrapper for a result that can either be successful or failed.
 *
 * @param <T> The type of the result value.
 */
public interface Try<T> {

    /**
     * @return A successful result with no value.
     */
    static Try<Void> success() {
        return new Success<>(null);
    }

    /**
     * @param value The value of the result.
     * @return A successful result with given value.
     * @param <T> The type of the result value.
     */
    static <T> Try<T> success(T value) {
        return new Success<>(value);
    }

    /**
     *
     * @param error The error that occurred.
     * @return A failed result with given error.
     * @param <T> The type of the result value.
     */
    static <T> Try<T> fail(Throwable error) {
        return new Failure<>(error);
    }

    /**
     *
     * @return {@code true} if the result is successful, {@code false} otherwise.
     */
    default boolean ok() {
        return !failed();
    }

    /**
     *
     * @return {@code true} if the result is failed, {@code false} otherwise.
     */
    boolean failed();

    /**
     *
     * @return The value of the result.
     */
    T get();

    /**
     *
     * @return The error that occurred.
     */
    Throwable cause();

    /**
     *
     * @return the value of the result if successful, or throws the error that occurred.
     * @throws Throwable if the result is failed.
     */
    default T orThrow() throws Throwable {
        if (failed()) {
            throw cause();
        }
        return get();
    }

    /**
     *
     * @param errorMapper a function that maps the error to a specific exception type.
     * @return the value of the result if successful, or throws the error that occurred.
     * @param <E> The type of the exception to throw.
     * @throws E if the result is failed.
     */
    default <E extends Exception> T orThrow(Function<Throwable, E> errorMapper) throws E {
        if (failed()) {
            throw errorMapper.apply(cause());
        }
        return get();
    }

    /**
     * A successful result.
     *
     * @param <T> The type of the result value.
     */
    final class Success<T> implements Try<T> {
        private final T value;

        private Success(@Nullable T value) {
            this.value = value;
        }

        @Override
        public boolean failed() {
            return false;
        }

        @Override
        public T get() {
            return value;
        }

        @Override
        public Throwable cause() {
            throw new UnsupportedOperationException("cause() on a successful Try");
        }
    }

    /**
     * A failed result.
     *
     * @param <T>
     */
    final class Failure<T> implements Try<T> {
        private final Throwable cause;

        private Failure(@NonNull Throwable cause) {
            this.cause = cause;
        }

        @Override
        public boolean failed() {
            return true;
        }

        @Override
        public T get() {
            throw new UnsupportedOperationException("get() on a failed Try");
        }

        @Override
        public Throwable cause() {
            return this.cause;
        }
    }
}
