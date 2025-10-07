/* Licensed under MIT

Copyright (c) 2025 Thomas Schulte-Bahrenberg */
package tschuba.basarix.ui;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.mock;

import com.vaadin.flow.component.Component;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Test class for {@link RefreshEvent}.
 */
class RefreshEventTest {
    @Test
    void testConstructorShouldThrowIfSourceIsNull() {
        assertThatIllegalArgumentException().isThrownBy(() -> new RefreshEvent(null, false)).withMessage("null source");
    }

    @Test
    void testConstructorShouldSucceedIfSourceIsNotNull() {
        assertThatNoException().isThrownBy(() -> new RefreshEvent(mock(Component.class), false));
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void testConstructorShouldSucceedForAnyFromClientValue(boolean fromClient) {
        assertThatNoException().isThrownBy(() -> new RefreshEvent(mock(Component.class), fromClient));
    }
}
