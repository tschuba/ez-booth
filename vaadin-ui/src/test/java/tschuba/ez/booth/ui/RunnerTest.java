/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.ui;

import static org.mockito.Mockito.mockStatic;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;

/**
 * Test class for {@link Runner}.
 */
class RunnerTest {
  @Test
  void testMainMethod() {
    try (MockedStatic<SpringApplication> mockedStatic = mockStatic(SpringApplication.class)) {
      String[] args = new String[] {"arg1", "arg2"};
      Runner.main(args);
      mockedStatic.verify(() -> SpringApplication.run(Runner.class, args));
    }
  }
}
