/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.ui.layouts.app;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import tschuba.ez.booth.data.BoothRepository;

public class BasicAppLayout extends CustomAppLayout {
    public BasicAppLayout(@Autowired BoothRepository booths) {
        super(booths, List.of());
    }
}
