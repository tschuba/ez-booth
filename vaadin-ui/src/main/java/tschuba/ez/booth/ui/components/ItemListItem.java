/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.ui.components;

import static com.vaadin.flow.theme.lumo.LumoUtility.*;
import static tschuba.ez.booth.ui.i18n.Formats.formats;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import lombok.Getter;
import tschuba.ez.booth.model.DataModel;
import tschuba.ez.booth.ui.renderer.VendorRenderer;

public class ItemListItem extends Div {
    @Getter private final DataModel.PurchaseItem item;
    private final Span vendorSpan;

    public ItemListItem(DataModel.PurchaseItem item) {
        this.item = item;

        addClassNames(Display.FLEX, AlignContent.CENTER, JustifyContent.BETWEEN);
        addClassNames(Padding.Horizontal.SMALL);

        Span dateTimeSpan = new Span();
        dateTimeSpan.setText(formats().dateTime(item.purchasedOn(), getLocale()));
        dateTimeSpan.addClassNames(FontSize.XXSMALL);
        add(dateTimeSpan);

        vendorSpan = new Span();
        vendorSpan.setVisible(false);
        String vendorText =
                VendorRenderer.of(getUI())
                        .keyToString(VendorRenderer.Format.Long)
                        .apply(item.vendor());
        vendorSpan.setText(vendorText);
        vendorSpan.addClassNames(FontSize.XXSMALL);
        add(vendorSpan);

        Span priceSpan = new Span();
        priceSpan.addClassNames(FontWeight.MEDIUM);
        priceSpan.setText(formats().currency(item.price(), getLocale()));
        add(priceSpan);
    }

    public void setShowVendor(boolean showVendor) {
        this.vendorSpan.setVisible(showVendor);
    }
}
