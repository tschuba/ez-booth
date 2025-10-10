/**
 * Copyright (c) 2023-2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.ui.util;

import static lombok.AccessLevel.PRIVATE;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.NoArgsConstructor;

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

    public static DataExchange dataExchange() {
        return DataExchange.INSTANCE;
    }

    @NoArgsConstructor(access = PRIVATE)
    public static class Http {
        private static final Http INSTANCE = new Http();

        public Support url(String authorityFragment) {
            return () -> "https?://" + authorityFragment + "(:\\d+)?";
        }

        public Support url() {
            return url("[\\S&&[^:]]+");
        }

        /**
         *
         * @return a regex pattern that matches a URL with an IPv4 address as authority
         */
        public Support urlIPv4() {
            return url(IPV4_PATTERN_FRAGMENT);
        }
    }

    @NoArgsConstructor(access = PRIVATE)
    public static class IpAddress {
        private static final IpAddress INSTANCE = new IpAddress();

        public Support v4() {
            return () -> IPV4_PATTERN_FRAGMENT;
        }
    }

    @NoArgsConstructor(access = PRIVATE)
    public static class DataExchange {
        private static final DataExchange INSTANCE = new DataExchange();

        public Support address() {
            return () -> "^[^\\s:\\/]+(:\\d+)?$";
        }

        public Support encodedAddress() {
            return () -> "[:]?[A-Za-z0-9+/=]+";
        }
    }

    @FunctionalInterface
    public interface Support {
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
        default Support wholeInput() {
            return () -> "^" + pattern() + "$";
        }

        default Support or(String otherPattern) {
            return () -> pattern() + "|" + otherPattern;
        }

        default Support or(Support otherPattern) {
            return or(otherPattern.pattern());
        }
    }
}
