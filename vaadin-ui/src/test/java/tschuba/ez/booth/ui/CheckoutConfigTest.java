/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.ui;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Test class for {@link CheckoutConfig}.
 */
class CheckoutConfigTest {
    @Test
    void testConstructor() {
        assertThatNoException().isThrownBy(() -> new CheckoutConfig(false, false));
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void testConfirmationRequired(boolean confirmationRequired) {
        CheckoutConfig config = new CheckoutConfig(confirmationRequired, false);
        assertThat(config.confirmationRequired()).isEqualTo(confirmationRequired);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void testPrintReceiptChecked(boolean printReceiptChecked) {
        CheckoutConfig config = new CheckoutConfig(false, printReceiptChecked);
        assertThat(config.printReceiptChecked()).isEqualTo(printReceiptChecked);
    }
}
