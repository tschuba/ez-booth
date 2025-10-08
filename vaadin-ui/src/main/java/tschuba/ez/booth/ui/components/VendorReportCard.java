/* Licensed under MIT

Copyright (c) 2025 Thomas Schulte-Bahrenberg */
package tschuba.ez.booth.ui.components;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.theme.lumo.LumoUtility.FontSize;
import jakarta.annotation.Nonnull;
import org.vaadin.lineawesome.LineAwesomeIcon;
import tschuba.ez.booth.model.DataModel;
import tschuba.ez.booth.services.ServiceModel;
import tschuba.ez.booth.ui.i18n.Formats;
import tschuba.ez.booth.ui.views.VendorReportPrintView;

import static com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import static tschuba.ez.booth.ui.i18n.TranslationKeys.VendorReportCard.*;

/**
 * A card component that displays vendor report data, including the total item count,
 * sum of sales, and a button to print the receipt.
 */
public class VendorReportCard extends VendorCard {

    private final Span itemCount = new Span();
    private final Span revenue = new Span();
    private final Button printButton = new Button();

    public VendorReportCard(@Nonnull ServiceModel.VendorReportData vendorData) {
        super(vendorData.vendor());

        itemCount.addClassNames(FontSize.XSMALL);
        itemCount.setText(Integer.toString(vendorData.items().size()));

        revenue.addClassNames(FontSize.XSMALL);
        revenue.setText(Formats.formats().currency(vendorData.salesSum(), getLocale()));

        printButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_CONTRAST);
        printButton.addClassNames(Margin.NONE);
        printButton.setIcon(LineAwesomeIcon.RECEIPT_SOLID.create());
        printButton.addClickListener(this::onClickPrint);

        add(itemCount, revenue, printButton);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);

        Tooltip.forComponent(printButton).withText(getTranslation(BUTTON_PRINT_RECEIPT__TOOLTIP));
        Tooltip.forComponent(revenue).withText(getTranslation(REVENUE__TOOLTIP));
        Tooltip.forComponent(itemCount).withText(getTranslation(ITEM_COUNT__TOOLTIP));
    }

    private void onClickPrint(ClickEvent<Button> clickEvent) {
        DataModel.Vendor.Key vendorKey = getVendor().key();
        VendorReportPrintView.newWindowFor(vendorKey);
    }
}
