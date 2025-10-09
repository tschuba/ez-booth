/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.ui.util;

import com.vaadin.flow.component.orderedlayout.ThemableLayout;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(staticName = "spacing")
public class Spacing {
    private static final String SPACING = "spacing";
    private static final String XSMALL = SPACING + "-xs";
    private static final String SMALL = SPACING + "-s";
    private static final String LARGE = SPACING + "-l";
    private static final String XLARGE = SPACING + "-xl";

    private final ThemableLayout themable;

    private void spacing(String spacing) {
        themable.setSpacing(false);
        themable.getThemeList().add(spacing);
    }

    public void xsmall() {
        spacing(XSMALL);
    }

    public void small() {
        spacing(SMALL);
    }

    public void standard() {
        spacing(SPACING);
    }

    public void large() {
        spacing(LARGE);
    }

    public void xlarge() {
        spacing(XLARGE);
    }
}
