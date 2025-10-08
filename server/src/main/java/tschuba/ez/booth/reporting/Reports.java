/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.reporting;

import java.time.LocalDate;
import lombok.NonNull;

/**
 * Utility class for report generation.
 */
public class Reports {
    private Reports() {}

    /**
     * Generates a HTML filename with the given prefix and the current date.
     *
     * @param prefix the prefix for the filename
     * @return the generated filename
     */
    public static String htmlFileName(@NonNull String prefix) {
        return prefix + "%s.%s".formatted(LocalDate.now(), "html");
    }
}
