/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.ui.layouts;

import static com.vaadin.flow.theme.lumo.LumoUtility.*;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Main;

public class OneColumnLayout extends BaseLayout {

  private final Main contentContainer;

  public OneColumnLayout() {
    contentContainer = new Main();
    contentContainer.addClassNames(
        Display.GRID,
        Grid.Column.COLUMNS_1,
        Gap.XLARGE,
        AlignItems.START,
        JustifyContent.START,
        Margin.Horizontal.AUTO);
    super.setContent(contentContainer);
  }

  public void setContent(Component content) {
    contentContainer.removeAll();
    contentContainer.add(content);
  }
}
