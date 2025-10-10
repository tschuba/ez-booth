/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.ui.components.event;

import com.vaadin.flow.component.ComponentEvent;
import lombok.Getter;
import tschuba.ez.booth.model.DataModel;

@Getter
public class BoothSavedEvent extends ComponentEvent<UpsertEventDialog> {
  private final DataModel.Booth booth;

  public BoothSavedEvent(UpsertEventDialog source, boolean fromClient, DataModel.Booth booth) {
    super(source, fromClient);
    this.booth = booth;
  }
}
