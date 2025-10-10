/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.ui.layouts.app;

import static java.util.Collections.emptyList;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import tschuba.ez.booth.services.BoothService;

public class BasicAppLayout extends CustomAppLayout {
    @Autowired
    public BasicAppLayout(@NonNull BoothService booths, @NonNull Environment environment) {
        super(booths, emptyList(), environment);
    }
}
