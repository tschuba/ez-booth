/* Licensed under MIT

Copyright (c) 2025 Thomas Schulte-Bahrenberg */
package tschuba.basarix.ui.components.checkout;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.shared.HasValidationProperties;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextFieldBase;
import lombok.Getter;
import org.vaadin.lineawesome.LineAwesomeIcon;
import tschuba.basarix.ui.components.event.EventSelection;
import tschuba.basarix.ui.util.UIUtil;
import tschuba.basarix.ui.views.CheckoutView;
import tschuba.commons.vaadin.i18n.I18N;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static com.vaadin.flow.theme.lumo.LumoUtility.FontSize;
import static com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import static tschuba.basarix.ui.i18n.TranslationKeys.CheckoutItemForm.*;

public class CheckoutItemForm extends Composite<HorizontalLayout> implements HasEnabled {
    @Getter
    private final IntegerField vendorField;
    @Getter
    private final BigDecimalField priceField;
    private final Button historyToggle;
    private final Button clearButton;

    public CheckoutItemForm() {
        vendorField = new IntegerField();
        vendorField.addThemeVariants();
        vendorField.addClassNames(Padding.Top.NONE, FontSize.XLARGE);
        vendorField.setRequiredIndicatorVisible(true);
        vendorField.setRequired(true);
        vendorField.setAutocorrect(true);
        vendorField.setAutofocus(true);
        vendorField.setAutoselect(true);
        vendorField.setMin(0);
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
        clearButton.addThemeVariants(ButtonVariant.LUMO_LARGE, ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_CONTRAST);
        clearButton.addClickListener(this::onClickClear);

        historyToggle = new Button();
        historyToggle.setIcon(LineAwesomeIcon.HISTORY_SOLID.create());
        historyToggle.addThemeVariants(ButtonVariant.LUMO_LARGE, ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_CONTRAST);
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
        priceField.setSuffixComponent(new Span(I18N.i18N().getCurrency(getLocale()).getSymbol()));

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
        UIUtil.traverseParents(this, parent -> {
            if (parent instanceof CheckoutView) {
                boolean visible = ((CheckoutView) parent).togglePurchaseHistory();
                LineAwesomeIcon icon = (visible) ? LineAwesomeIcon.KEYBOARD : LineAwesomeIcon.SCROLL_SOLID;
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
            Stream.of(vendorField, priceField).filter(HasValidationProperties::isInvalid).findFirst().ifPresentOrElse(Focusable::focus, this::processAddItem);
        }
    }

    private void processAddItem() {
        EventSelection.get().ifPresent(eventKey -> {
            BigDecimal price = priceField.getValue();
            Integer vendorId = vendorField.getValue();
            priceField.clear();
            vendorField.focus();
            fireEvent(new AddItemEvent(this, false, vendorId, price.doubleValue()));
        });
    }

    @Getter
    public static class AddItemEvent extends ComponentEvent<CheckoutItemForm> {
        private final Integer vendorId;
        private final Double price;

        private AddItemEvent(CheckoutItemForm source, boolean fromClient, Integer vendorId, Double price) {
            super(source, fromClient);
            this.vendorId = vendorId;
            this.price = price;
        }

    }
}
