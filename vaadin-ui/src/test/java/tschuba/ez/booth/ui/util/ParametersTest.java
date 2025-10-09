/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.ui.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.Mockito.mock;

import com.vaadin.flow.router.RouteParameters;
import org.junit.jupiter.api.Test;

/**
 * Test class for {@link Routing.Parameters}.
 */
class ParametersTest {
    @Test
    void testParserFactoryMethod() {
        assertThatNoException()
                .isThrownBy(
                        () ->
                                assertThat(Routing.Parameters.parser(mock(RouteParameters.class)))
                                        .isNotNull());
    }

    @Test
    void testBuilderFactoryMethod() {
        assertThatNoException()
                .isThrownBy(() -> assertThat(Routing.Parameters.builder()).isNotNull());
    }
}
