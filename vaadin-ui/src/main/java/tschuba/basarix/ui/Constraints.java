/* Licensed under MIT

Copyright (c) 2025 Thomas Schulte-Bahrenberg */
package tschuba.basarix.ui;

import static lombok.AccessLevel.PRIVATE;
import static tschuba.basarix.common.Patterns.dataSync;
import static tschuba.basarix.common.Patterns.http;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public class Constraints {

    @NoArgsConstructor(access = PRIVATE)
    public static class Events {

        @NoArgsConstructor(access = PRIVATE)
        public static class Description {
            public static final String ALLOWED_CHARS_PATTERN = "\\w|[- ÄÖÜäöüß]";
        }
    }

    @NoArgsConstructor(access = PRIVATE)
    public static class DataSync {

        @NoArgsConstructor(access = PRIVATE)
        public static class Subscriber {
            public static final String SYNC_URL_PATTERN = http().url().or(dataSync().encodedHostAddress()).wholeInput().pattern();
        }
    }
}
