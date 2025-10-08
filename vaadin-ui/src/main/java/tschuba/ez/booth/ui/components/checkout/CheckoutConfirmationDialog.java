/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.ui.components.checkout;

import static com.vaadin.flow.theme.lumo.LumoUtility.*;
import static tschuba.ez.booth.i18n.Formats.formats;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import java.math.BigDecimal;
import tschuba.ez.booth.model.DataModel;
import tschuba.ez.booth.services.ServiceModel;
import tschuba.ez.booth.ui.CheckoutConfig;

@SpringComponent
@UIScope
public class CheckoutConfirmationDialog extends Dialog {
    private final Span textSpan;
    private final Span valueSpan;
    private final Checkbox checkboxPrintReceipt;
    private final Button confirmButton;
    private final Button cancelButton;
    private ServiceModel.Checkout checkout;

    public CheckoutConfirmationDialog(final CheckoutConfig config) {
        textSpan = new Span();
        textSpan.addClassNames(Margin.Right.SMALL);

        valueSpan = new Span();
        valueSpan.addClassNames(FontSize.XLARGE, FontWeight.EXTRABOLD);

        cancelButton = new Button();
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        cancelButton.addClickListener(this::onClickCancel);

        checkboxPrintReceipt = new Checkbox();
        checkboxPrintReceipt.setValue(config.printReceiptChecked());

        confirmButton = new Button();
        confirmButton.setDisableOnClick(true);
        confirmButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        confirmButton.addClickListener(this::onClickConfirm);

        Footer footer = new Footer(cancelButton, checkboxPrintReceipt, confirmButton);
        footer.addClassNames(
                Display.FLEX, AlignItems.CENTER, JustifyContent.BETWEEN, Margin.Top.MEDIUM);

        add(textSpan, valueSpan, footer);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);

        setHeaderTitle(getTranslation(TITLE));
        textSpan.setText(getTranslation(TEXT));
        checkboxPrintReceipt.setLabel(getTranslation(PRINT_CHECKBOX__LABEL));
        confirmButton.setText(getTranslation(CONFIRM_BUTTON__TEXT));
        cancelButton.setText(getTranslation(CANCEL_BUTTON__TEXT));
    }

    private void setCheckout(ServiceModel.Checkout checkout) {
        this.checkout = checkout;

        BigDecimal checkoutSum =
                checkout.items().stream()
                        .map(DataModel.PurchaseItem::price)
                        .reduce(BigDecimal::add)
                        .orElse(BigDecimal.ZERO);
        valueSpan.setText(formats().currency(checkoutSum, getLocale()));
    }

    public void open(ServiceModel.Checkout checkout) {
        setCheckout(checkout);
        super.open();
    }

    public void addCheckoutConfirmedListener(
            ComponentEventListener<CheckoutConfirmedEvent> listener) {
        this.addListener(CheckoutConfirmedEvent.class, listener);
    }

    private void onClickConfirm(ClickEvent<Button> event) {
        try {
            ServiceModel.Checkout finalCheckout =
                    this.checkout.toBuilder().printReceipt(checkboxPrintReceipt.getValue()).build();
            fireEvent(new CheckoutConfirmedEvent(this, event.isFromClient(), finalCheckout));
            this.close();
        } finally {
            confirmButton.setEnabled(true);
        }
    }

    private void onClickCancel(ClickEvent<Button> event) {
        this.close();
    }
}
