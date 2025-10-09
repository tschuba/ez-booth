/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.ui.util;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.vaadin.flow.component.icon.SvgIcon;
import org.junit.jupiter.api.Test;

/**
 * Test class for {@link Icons}.
 */
class IconsTest {
    @Test
    void testLargeShouldThrowIfIconIsNull() {
        assertThatNullPointerException()
                .isThrownBy(() -> Icons.large(null))
                .withMessage("icon is marked non-null but is null");
    }

    @Test
    void testLargeShouldReturnIconWithSizeSet() {
        SvgIcon iconMock = mock(SvgIcon.class);
        SvgIcon actualIcon = Icons.large(iconMock);

        assertThat(actualIcon).isEqualTo(iconMock);
        verify(iconMock).setSize(Icons.LUMO_ICON_SIZE_L);
    }
}
