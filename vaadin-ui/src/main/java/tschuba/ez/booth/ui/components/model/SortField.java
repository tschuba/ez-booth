/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.ui.components.model;

public class SortField<F> {
  public enum Order {
    Ascending,
    Descending
  }

  private final F field;
  private final Order order;

  public SortField(F field, Order order) {
    this.field = field;
    this.order = order;
  }

  public F getField() {
    return field;
  }

  public Order getOrder() {
    return order;
  }
}
