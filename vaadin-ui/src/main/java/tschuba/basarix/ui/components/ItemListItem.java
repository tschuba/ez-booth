/* Licensed under MIT

Copyright (c) 2025 Thomas Schulte-Bahrenberg */
package tschuba.basarix.ui.components;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import lombok.Getter;
import tschuba.basarix.data.model.Item;
import tschuba.basarix.ui.renderer.VendorRenderer;

import static com.vaadin.flow.theme.lumo.LumoUtility.*;
import static tschuba.commons.vaadin.i18n.Formats.formats;

public class ItemListItem extends Div {
    @Getter
    private final Item item;
    private final Span vendorSpan;

    public ItemListItem(Item item) {
        this.item = item;

        addClassNames(Display.FLEX, AlignContent.CENTER, JustifyContent.BETWEEN);
        addClassNames(Padding.Horizontal.SMALL);

        Span dateTimeSpan = new Span();
        dateTimeSpan.setText(formats().dateTime(item.getDateTime(), getLocale()));
        dateTimeSpan.addClassNames(FontSize.XXSMALL);
        add(dateTimeSpan);

        vendorSpan = new Span();
        vendorSpan.setVisible(false);
        String vendorText = VendorRenderer.of(getUI()).keyToString(VendorRenderer.Format.Long).apply(item.getVendor());
        vendorSpan.setText(vendorText);
        vendorSpan.addClassNames(FontSize.XXSMALL);
        add(vendorSpan);

        Span priceSpan = new Span();
        priceSpan.addClassNames(FontWeight.MEDIUM);
        priceSpan.setText(formats().currency(item.getPrice(), getLocale()));
        add(priceSpan);
    }

    public void setShowVendor(boolean showVendor) {
        this.vendorSpan.setVisible(showVendor);
    }

}
