/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth;

import java.util.UUID;

/**
 * Utility class for generating unique IDs.
 */
public class Ids {
    private Ids() {}

    /**
     * Generates a new random UUID as a string.
     *
     * @return a new random UUID string
     */
    public static String UUID() {
        return UUID.randomUUID().toString();
    }
}
