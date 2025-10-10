/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;

public class RefreshEvent extends ComponentEvent<Component> {
  /**
   * Creates a new event using the given source and indicator whether the
   * event originated from the client side or the server side.
   *
   * @param source     the source component
   * @param fromClient <code>true</code> if the event originated from the client
   *                   side, <code>false</code> otherwise
   */
  public RefreshEvent(Component source, boolean fromClient) {
    super(source, fromClient);
  }
}
