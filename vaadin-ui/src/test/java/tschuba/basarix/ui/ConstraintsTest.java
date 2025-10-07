/* Licensed under MIT

Copyright (c) 2025 Thomas Schulte-Bahrenberg */
package tschuba.basarix.ui;

import static org.assertj.core.api.Assertions.assertThat;
import static tschuba.basarix.ui.Constraints.DataSync.Subscriber.SYNC_URL_PATTERN;

import java.util.regex.Pattern;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Test class for the {@link Constraints}.
 */
class ConstraintsTest {

    @ParameterizedTest
    @ValueSource(strings = {"https://example.com", "http://example.com", "https://example.com/path/to/resource", "http://example.com/path/to/resource", "https://example.com:8080", "http://example.com:8080", "https://example.com/path/to/resource?query=param", "http://example.com/path/to/resource?query=param", "https://example.com/path/to/resource#fragment", "http://example.com/path/to/resource#fragment"
    })
    void testDataSyncSubscriberSyncUrlPatternShouldMatchValidInput(String input) {
        assertThat(Pattern.matches(SYNC_URL_PATTERN, input)).as("Input should match the sync URL pattern").isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid-url", "ftp://example.com", "https://example.com:invalid-port"
    })
    void testDataSyncSubscriberSyncUrlPatternShouldNotMatchValidInput(String input) {
        assertThat(Pattern.matches(SYNC_URL_PATTERN, input)).as("Input should not match the sync URL pattern").isFalse();
    }
}
