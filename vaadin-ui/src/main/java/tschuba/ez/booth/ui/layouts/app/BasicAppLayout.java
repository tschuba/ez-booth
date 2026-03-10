/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.ui.layouts.app;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import tschuba.ez.booth.services.BoothService;

import java.util.List;

public class BasicAppLayout extends CustomAppLayout {
  @Autowired
  public BasicAppLayout(@NonNull BoothService booths, @NonNull Environment environment) {
    super(booths, List.of(MainMenuItem.DATA_EXCHANGE_VIEW.get()), environment);
  }
}
