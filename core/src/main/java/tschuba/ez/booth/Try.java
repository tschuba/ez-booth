/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth;

import jakarta.annotation.Nullable;
import java.util.function.Function;
import java.util.function.Supplier;
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
   * Executes the given runnable and returns a Try representing the outcome.
   *
   * @param runnable The runnable to execute.
   * @return A successful Try if the runnable completes without throwing an exception,
   *         or a failed Try if an exception is thrown.
   */
  static Try<Void> tryTo(@NonNull Runnable runnable) {
    try {
      runnable.run();
      return success();
    } catch (Throwable ex) {
      return fail(ex);
    }
  }

  /**
   * Executes the given supplier and returns a Try representing the outcome.
   * @param <V> The type of the result value.
   * @param supplier The supplier to execute.
   * @return A successful Try with the supplied value if the supplier completes without throwing an exception,
   *         or a failed Try containing the thrown exception if an exception is thrown during execution.
   */
  static <V> Try<V> tryTo(@NonNull Supplier<V> supplier) {
    try {
      return success(supplier.get());
    } catch (Throwable ex) {
      return fail(ex);
    }
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
   * @param defaultValue The default value to return if the result is failed.
   * @return the value of the result if successful, or the given default value if failed.
   */
  default T orElse(T defaultValue) {
    return ok() ? get() : defaultValue;
  }

  /**
   * @param defaultValueSupplier A supplier that provides the default value to return if the result is failed.
   * @return the value of the result if successful, or the value provided by the given supplier if failed.
   */
  default T or(@NonNull Supplier<T> defaultValueSupplier) {
    return ok() ? get() : defaultValueSupplier.get();
  }

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
  default <E extends Exception> T orThrow(@NonNull Function<Throwable, E> errorMapper) throws E {
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
