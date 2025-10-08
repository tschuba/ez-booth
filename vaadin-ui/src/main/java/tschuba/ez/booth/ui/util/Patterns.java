/* Licensed under MIT

Copyright (c) 2023 Thomas Schulte-Bahrenberg */
package tschuba.ez.booth.ui.util;

import lombok.NoArgsConstructor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static lombok.AccessLevel.PRIVATE;

/** Helper class providing common Regex patterns. */
@NoArgsConstructor(access = PRIVATE)
public class Patterns {
    private static final String IPV4_PATTERN_FRAGMENT = "(\\d{1,3}\\.){3}\\d{1,3}";

    public static Http http() {
        return Http.INSTANCE;
    }

    public static IpAddress ipAddress() {
        return IpAddress.INSTANCE;
    }

    public static DataSync dataSync() {
        return DataSync.INSTANCE;
    }

    @NoArgsConstructor(access = PRIVATE)
    public static class Http {
        private static final Http INSTANCE = new Http();

        public PatternSupport url(String authorityFragment) {
            return () -> "https?://" + authorityFragment + "(:\\d+)?";
        }

        public PatternSupport url() {
            return url("[\\S&&[^:]]+");
        }

        /**
         *
         * @return a regex pattern that matches a URL with an IPv4 address as authority
         */
        public PatternSupport urlIPv4() {
            return url(IPV4_PATTERN_FRAGMENT);
        }
    }

    @NoArgsConstructor(access = PRIVATE)
    public static class IpAddress {
        private static final IpAddress INSTANCE = new IpAddress();

        public PatternSupport v4() {
            return () -> IPV4_PATTERN_FRAGMENT;
        }
    }

    @NoArgsConstructor(access = PRIVATE)
    public static class DataSync {
        private static final DataSync INSTANCE = new DataSync();

        public PatternSupport encodedHostAddress() {
            return () -> "[#@][A-Za-z0-9+/=]+";
        }
    }

    @FunctionalInterface
    public interface PatternSupport {
        String pattern();

        default Pattern compiled() {
            return Pattern.compile(pattern());
        }

        default Matcher matcher(CharSequence input) {
            return compiled().matcher(input);
        }

        /**
         * @return a regex pattern that matches the whole input string
         */
        default PatternSupport wholeInput() {
            return () -> "^" + pattern() + "$";
        }

        default PatternSupport or(String otherPattern) {
            return () -> pattern() + "|" + otherPattern;
        }

        default PatternSupport or(PatternSupport otherPattern) {
            return or(otherPattern.pattern());
        }
    }
}