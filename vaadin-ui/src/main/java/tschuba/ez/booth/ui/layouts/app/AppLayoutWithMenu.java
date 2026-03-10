/**
 * Copyright (c) 2025-2026 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.ui.layouts.app;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import tschuba.ez.booth.services.BoothService;

import java.util.List;

public class AppLayoutWithMenu extends CustomAppLayout {
  @Autowired
  public AppLayoutWithMenu(@NonNull BoothService booths, @NonNull Environment environment) {
    super(
        booths,
        List.of(
            MainMenuItem.BOOTH_SELECTION_VIEW.get(),
            MainMenuItem.CHECKOUT_VIEW.get(),
            MainMenuItem.VENDOR_REPORT_VIEW.get(),
            MainMenuItem.DATA_EXCHANGE_VIEW.get()
        ),
        environment);
  }
}
