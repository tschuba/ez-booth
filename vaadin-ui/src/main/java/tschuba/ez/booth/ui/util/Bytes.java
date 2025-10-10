/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.ui.util;

import java.nio.ByteBuffer;

/**
 * Helper class related to bytes.
 */
public record Bytes(byte byteValue) {

    /**
     * Creates a {@link Bytes} instance from an unsigned integer (0-255).
     *
     * @param unsigned the unsigned integer
     * @return a {@link Bytes} instance representing the given unsigned integer
     * @throws IllegalArgumentException if the given unsigned is less than 0 or greater than 255
     */
    public static Bytes fromUnsignedInt(int unsigned) {
        if (unsigned < 0) {
            throw new IllegalArgumentException(
                    "Given unsigned is less than 0: %s".formatted(unsigned));
        }
        if (unsigned > Byte.MAX_VALUE - Byte.MIN_VALUE) {
            throw new IllegalArgumentException(
                    "Given unsigned is greater than 255: %s".formatted(unsigned));
        }

        ByteBuffer buffer = ByteBuffer.allocate(4).putInt(unsigned);
        byte byteValue = buffer.get(3);

        return new Bytes(byteValue);
    }
}
