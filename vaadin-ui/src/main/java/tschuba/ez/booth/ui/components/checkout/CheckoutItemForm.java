/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.ui.components.checkout;

import static com.vaadin.flow.theme.lumo.LumoUtility.FontSize;
import static com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import static tschuba.ez.booth.i18n.TranslationKeys.CheckoutItemForm.*;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.shared.HasValidationProperties;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldBase;
import java.math.BigDecimal;
import java.util.stream.Stream;
import lombok.Getter;
import org.vaadin.lineawesome.LineAwesomeIcon;
import tschuba.ez.booth.i18n.I18N;
import tschuba.ez.booth.ui.Constraints;
import tschuba.ez.booth.ui.components.event.BoothSelection;
import tschuba.ez.booth.ui.util.UIUtil;
import tschuba.ez.booth.ui.views.CheckoutView;

public class CheckoutItemForm extends Composite<HorizontalLayout> implements HasEnabled {
    @Getter private final TextField vendorField;
    @Getter private final BigDecimalField priceField;
    private final Button historyToggle;
    private final Button clearButton;

    public CheckoutItemForm() {
        vendorField = new TextField();
        vendorField.addThemeVariants();
        vendorField.addClassNames(Padding.Top.NONE, FontSize.XLARGE);
        vendorField.setRequiredIndicatorVisible(true);
        vendorField.setRequired(true);
        vendorField.setAutocorrect(true);
        vendorField.setAutofocus(true);
        vendorField.setAutoselect(true);
        vendorField.setAllowedCharPattern(Constraints.Vendors.ALLOWED_CHARS_PATTERN);
        vendorField.addKeyDownListener(Key.ENTER, this::onEnterOnVendorField);

        priceField = new BigDecimalField();
        priceField.addClassNames(Padding.Top.NONE, FontSize.XLARGE);
        priceField.setAutocorrect(true);
        priceField.setAutoselect(true);
        priceField.setClearButtonVisible(true);
        priceField.setRequired(true);
        priceField.setRequiredIndicatorVisible(true);
        priceField.addKeyDownListener(Key.ENTER, event -> beforeAddItem(event.getSource()));

        clearButton = new Button();
        clearButton.setIcon(LineAwesomeIcon.UNDO_ALT_SOLID.create());
        clearButton.addThemeVariants(
                ButtonVariant.LUMO_LARGE, ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_CONTRAST);
        clearButton.addClickListener(this::onClickClear);

        historyToggle = new Button();
        historyToggle.setIcon(LineAwesomeIcon.HISTORY_SOLID.create());
        historyToggle.addThemeVariants(
                ButtonVariant.LUMO_LARGE, ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_CONTRAST);
        historyToggle.addClickListener(this::onHistoryToggleClick);

        HorizontalLayout content = getContent();
        content.add(vendorField, priceField, clearButton, historyToggle);
        content.setAlignItems(FlexComponent.Alignment.BASELINE);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);

        vendorField.setLabel(getTranslation(VENDOR_FIELD__LABEL));
        vendorField.setPlaceholder(getTranslation(VENDOR_FIELD__PLACEHOLDER));

        priceField.setLabel(getTranslation(PRICE_FIELD__LABEL));
        priceField.setPlaceholder(getTranslation(PRICE_FIELD__PLACEHOLDER));
        priceField.setSuffixComponent(new Span(I18N.current().currency(getLocale()).getSymbol()));

        Tooltip.forComponent(clearButton).setText(getTranslation(CLEAR_BUTTON__TOOLTIP));
        Tooltip.forComponent(historyToggle).setText(getTranslation(HISTORY_TOGGLE_BUTTON__TOOLTIP));
    }

    public void valueConfirmed(AbstractField<?, ?> field) {
        if (vendorField.equals(field)) {
            onCompleteVendorId();
        } else if (priceField.equals(field)) {
            beforeAddItem(field);
        }
    }

    public void clear() {
        priceField.clear();
        vendorField.focus();
    }

    public void addAddItemEventListener(ComponentEventListener<AddItemEvent> listener) {
        addListener(AddItemEvent.class, listener);
    }

    private void onClickClear(ClickEvent<Button> event) {
        priceField.clear();
        vendorField.focus();
        vendorField.clear();
    }

    private void onEnterOnVendorField(KeyDownEvent event) {
        onCompleteVendorId();
    }

    private void onCompleteVendorId() {
        priceField.focus();
    }

    private void onHistoryToggleClick(ClickEvent<Button> event) {
        UIUtil.traverseParents(
                this,
                parent -> {
                    if (parent instanceof CheckoutView) {
                        boolean visible = ((CheckoutView) parent).togglePurchaseHistory();
                        LineAwesomeIcon icon =
                                (visible) ? LineAwesomeIcon.KEYBOARD : LineAwesomeIcon.SCROLL_SOLID;
                        historyToggle.setIcon(icon.create());
                        return false;
                    }
                    return true;
                });
    }

    private void beforeAddItem(Component eventSource) {
        if (eventSource instanceof TextFieldBase<?, ?> textField && textField.isInvalid()) {
            textField.focus();
        } else {
            Stream.of(vendorField, priceField)
                    .filter(HasValidationProperties::isInvalid)
                    .findFirst()
                    .ifPresentOrElse(Focusable::focus, this::processAddItem);
        }
    }

    private void processAddItem() {
        BoothSelection.get()
                .ifPresent(
                        eventKey -> {
                            BigDecimal price = priceField.getValue();
                            String vendorId = vendorField.getValue();
                            priceField.clear();
                            vendorField.focus();
                            fireEvent(new AddItemEvent(this, false, vendorId, price));
                        });
    }

    @Getter
    public static class AddItemEvent extends ComponentEvent<CheckoutItemForm> {
        private final String vendorId;
        private final BigDecimal price;

        private AddItemEvent(
                CheckoutItemForm source, boolean fromClient, String vendorId, BigDecimal price) {
            super(source, fromClient);
            this.vendorId = vendorId;
            this.price = price;
        }
    }
}
