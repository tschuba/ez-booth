/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.ui.util;

import com.vaadin.flow.component.icon.AbstractIcon;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Icons {

    static final String LUMO_ICON_SIZE_L = "var(--lumo-icon-size-l)";

    public static <T extends AbstractIcon<?>> T large(@NonNull T icon) {
        icon.setSize(LUMO_ICON_SIZE_L);
        return icon;
    }
}
