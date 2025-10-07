/* Licensed under MIT

Copyright (c) 2025 Thomas Schulte-Bahrenberg */
package tschuba.basarix.ui.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.Mockito.mock;

import com.vaadin.flow.router.RouteParameters;
import org.junit.jupiter.api.Test;

/**
 * Test class for {@link RoutingParameters}.
 */
class RoutingParametersTest {
    @Test
    void testParserFactoryMethod() {
        assertThatNoException().isThrownBy(() -> assertThat(RoutingParameters.parser(mock(RouteParameters.class))).isNotNull());
    }

    @Test
    void testBuilderFactoryMethod() {
        assertThatNoException().isThrownBy(() -> assertThat(RoutingParameters.builder()).isNotNull());
    }
}
