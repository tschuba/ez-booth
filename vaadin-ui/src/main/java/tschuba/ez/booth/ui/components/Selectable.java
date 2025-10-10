/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.ui.components;

import static com.vaadin.flow.theme.lumo.LumoUtility.Background;

import com.vaadin.flow.component.HasStyle;

public interface Selectable extends HasStyle {
  default void select() {
    addClassName(Background.PRIMARY_10);
  }

  default void unselect() {
    removeClassName(Background.PRIMARY_10);
  }

  default boolean isSelected() {
    return hasClassName(Background.PRIMARY_10);
  }
}
