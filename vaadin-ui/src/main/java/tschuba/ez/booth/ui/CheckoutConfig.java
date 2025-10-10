/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.ui;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("ez.basar.ui.checkout")
public class CheckoutConfig {

  private final boolean confirmationRequired;
  private final boolean printReceiptChecked;

  public CheckoutConfig(boolean confirmationRequired, boolean printReceiptChecked) {
    this.confirmationRequired = confirmationRequired;
    this.printReceiptChecked = printReceiptChecked;
  }

  public boolean confirmationRequired() {
    return confirmationRequired;
  }

  public boolean printReceiptChecked() {
    return printReceiptChecked;
  }
}
