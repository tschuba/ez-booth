/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.ui.components.checkout;

import com.vaadin.flow.theme.lumo.LumoUtility;
import tschuba.ez.booth.model.DataModel;
import tschuba.ez.booth.ui.components.ItemListItem;

public class ReportPrintViewItemCard extends ItemListItem {
  public ReportPrintViewItemCard(DataModel.PurchaseItem item) {
    super(item);
    addClassNames(LumoUtility.FlexDirection.COLUMN_REVERSE, LumoUtility.AlignItems.END);
  }
}
