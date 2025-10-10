/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.ui;

import static org.assertj.core.api.Assertions.assertThat;
import static tschuba.ez.booth.ui.Constraints.DataExchange.Transfer.ADDRESS_PATTERN;

import java.util.regex.Pattern;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Test class for the {@link Constraints}.
 */
class ConstraintsTest {

    @ParameterizedTest
    @ValueSource(
            strings = {
                "example.com",
                "example.com",
                "example.com:8080",
                "example.com:8080",
                "192.168.0.1:8080",
            })
    void testDataExchangeAddressPatternShouldMatchValidInput(String input) {
        assertThat(Pattern.matches(ADDRESS_PATTERN, input))
                .as("Input should match the data exchange address pattern")
                .isTrue();
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "http://invalid-url",
                "https://invalid-url",
                "ftp://example.com",
                "https://example.com:invalid-port",
                "example.com/path/to/resource"
            })
    void testDataExchangeAddressPatternShouldNotMatchInvalidInput(String input) {
        assertThat(Pattern.matches(ADDRESS_PATTERN, input))
                .as("Input should not match the data exchange address pattern")
                .isFalse();
    }
}
