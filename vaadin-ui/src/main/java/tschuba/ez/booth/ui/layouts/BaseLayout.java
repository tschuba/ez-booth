/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.ui.layouts;

import static com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import static com.vaadin.flow.theme.lumo.LumoUtility.*;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.HasDynamicTitle;
import java.util.Objects;
import tschuba.ez.booth.ui.components.PageTitle;
import tschuba.ez.booth.ui.components.event.BoothSelection;
import tschuba.ez.booth.ui.util.UIUtil;

public class BaseLayout extends Composite<Div> implements BeforeEnterObserver, HasDynamicTitle {

  private final PageTitle title;
  private final Div contentContainer;
  private final HorizontalLayout titleContainer;
  private Component content;

  public BaseLayout() {
    Div root = getContent();
    root.addClassNames(
        Display.FLEX, FlexDirection.COLUMN, Flex.GROW_NONE, JustifyContent.START, Height.FULL);

    contentContainer = new Div();
    contentContainer.addClassNames(Display.GRID, AlignItems.START, JustifyContent.CENTER);

    root.add(contentContainer);

    titleContainer = new HorizontalLayout(Alignment.CENTER);
    contentContainer.add(titleContainer);

    title = new PageTitle();
    titleContainer.add(title);
  }

  public void setTitle(String title) {
    this.title.setText(title);
  }

  public void setTitle(String title, Component titleSuffix) {
    this.titleContainer
        .getChildren()
        .filter(child -> !Objects.equals(child, this.title))
        .forEach(Component::removeFromParent);
    setTitle(title);
    this.titleContainer.add(titleSuffix);
  }

  protected void setContent(Component content) {
    if (this.content != null) {
      contentContainer.replace(this.content, content);
    } else {
      contentContainer.add(content);
    }
    this.content = content;
  }

  @Override
  public void beforeEnter(BeforeEnterEvent event) {
    BoothSelection.checkBeforeEnter(event, this);
  }

  @Override
  public String getPageTitle() {
    return UIUtil.pageTitle(this);
  }
}
