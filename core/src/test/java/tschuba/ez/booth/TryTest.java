/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.function.Function;
import org.junit.jupiter.api.Test;

/**
 * Test class for the {@link Try} class.
 */
class TryTest {
  @Test
  void testSuccessOfVoid() {
    assertThatNoException().isThrownBy(() -> assertThat(Try.success()).isNotNull());
  }

  @Test
  void testSuccessOfNull() {
    assertThatNoException()
        .isThrownBy(() -> assertThat(Try.success(null)).isNotNull().extracting(Try::get).isNull());
  }

  @Test
  void testSuccessOfValue() {
    assertThatNoException()
        .isThrownBy(
            () -> assertThat(Try.success("")).isNotNull().extracting(Try::get).isEqualTo(""));
  }

  @Test
  void testFail() {
    Exception exceptionMock = mock(Exception.class);
    assertThatNoException()
        .isThrownBy(
            () ->
                assertThat(Try.fail(exceptionMock))
                    .isNotNull()
                    .extracting(Try::cause)
                    .isEqualTo(exceptionMock));
  }

  @Test
  void testOkShouldReturnTrueForSuccess() {
    assertThat(Try.success().ok()).isTrue();
  }

  @Test
  void testOkShouldReturnFalseForFailure() {
    assertThat(Try.fail(mock(Exception.class)).ok()).isFalse();
  }

  @Test
  void testFailedShouldReturnFalseForSuccess() {
    assertThat(Try.success().failed()).isFalse();
  }

  @Test
  void testFailedShouldReturnTrueForFailure() {
    assertThat(Try.fail(mock(Exception.class)).failed()).isTrue();
  }

  @Test
  void testGetShouldReturnNullForSuccessOfVoid() {
    assertThat(Try.success().get()).isNull();
  }

  @Test
  void testGetShouldReturnValueForSuccessOfValue() {
    assertThat(Try.success("").get()).isEqualTo("");
  }

  @Test
  void testGetShouldThrowForFailure() {
    assertThatExceptionOfType(UnsupportedOperationException.class)
        .isThrownBy(() -> Try.fail(mock(Exception.class)).get())
        .withMessage("get() on a failed Try");
  }

  @Test
  void testOrThrowShouldReturnValueForSuccess() {
    assertThatNoException().isThrownBy(() -> assertThat(Try.success("").orThrow()).isEqualTo(""));
  }

  @Test
  void testOrThrowShouldThrowCauseForFailure() {
    Exception causeMock = mock(Exception.class);
    assertThatExceptionOfType(Exception.class).isThrownBy(() -> Try.fail(causeMock).orThrow());
  }

  @Test
  void testOrThrowWithMapper() {
    Function<Throwable, Exception> mapperMock = mock(Function.class);
    Exception causeMock = mock(Exception.class);
    assertThatExceptionOfType(Exception.class)
        .isThrownBy(() -> Try.fail(causeMock).orThrow(mapperMock));
    verify(mapperMock).apply(causeMock);
  }
}
