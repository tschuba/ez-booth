/**
 * Copyright (c) 2026 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.ui.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.popover.Popover;
import com.vaadin.flow.component.popover.PopoverPosition;
import com.vaadin.flow.component.progressbar.ProgressBar;

/**
 * A simple busy indicator component that can be shown during long-running operations.
 */
public class BusyIndicator extends Composite<Popover> {

  private final Span busyIndicatorText = new Span();

  public BusyIndicator() {
    ProgressBar progressBar = new ProgressBar();
    progressBar.setIndeterminate(true);

    Popover popover = getContent();
    popover.setTarget(this);
    popover.setModal(true);
    popover.setBackdropVisible(true);
    popover.setOpenOnClick(false);
    popover.setOpenOnFocus(false);
    popover.setOpenOnHover(false);
    popover.setCloseOnEsc(false);
    popover.setCloseOnOutsideClick(false);
    popover.setPosition(PopoverPosition.TOP);
    popover.add(new VerticalLayout(busyIndicatorText, progressBar));
  }

  public void setTarget(Component target) {
    getContent().setTarget(target);
  }

  public void setText(String text) {
    busyIndicatorText.setText(text);
  }

  public void open() {
    getContent().open();
  }

  public void close() {
    getContent().close();
  }
}
