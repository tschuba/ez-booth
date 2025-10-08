/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.ui.components.checkout;

import static com.vaadin.flow.theme.lumo.LumoUtility.*;
import static tschuba.ez.booth.i18n.TranslationKeys.PurchaseSummary.*;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.lineawesome.LineAwesomeIcon;
import tschuba.ez.booth.i18n.I18N;
import tschuba.ez.booth.model.DataModel;
import tschuba.ez.booth.services.ServiceModel;
import tschuba.ez.booth.ui.CheckoutConfig;

@SpringComponent
@UIScope
public class PurchaseSummary extends Div {
    private final CheckoutConfig config;

    private final H3 header;
    private final Button checkoutButton;
    private final Span purchaseSumSpan;
    private final Div vendorContainer;
    private final CheckoutConfirmationDialog confirmationDialog;
    private final Span itemCountSpan;

    @Autowired
    public PurchaseSummary(
            @NonNull CheckoutConfirmationDialog confirmationDialog,
            @NonNull CheckoutConfig config) {
        this.config = config;
        this.confirmationDialog = confirmationDialog;

        addClassNames(
                Display.GRID,
                Background.CONTRAST_5,
                BoxSizing.BORDER,
                Padding.LARGE,
                BorderRadius.LARGE,
                Position.STICKY);

        Header headerSection = new Header();
        headerSection.addClassNames(Display.FLEX, AlignItems.CENTER, JustifyContent.BETWEEN);
        header = new H3();
        header.addClassNames(Margin.NONE);

        purchaseSumSpan = new Span();
        purchaseSumSpan.addClassNames(Margin.Left.XLARGE, Padding.SMALL);
        purchaseSumSpan.addClassNames(Background.SUCCESS_50, BorderRadius.LARGE, FontSize.XLARGE);

        itemCountSpan = new Span();

        checkoutButton = new Button();
        checkoutButton.setIcon(LineAwesomeIcon.WALLET_SOLID.create());
        checkoutButton.addThemeVariants(
                ButtonVariant.LUMO_ICON,
                ButtonVariant.LUMO_SUCCESS,
                ButtonVariant.LUMO_PRIMARY,
                ButtonVariant.LUMO_LARGE);
        checkoutButton.addClassNames(
                FontSize.LARGE, Border.ALL, BorderRadius.MEDIUM, BorderColor.SUCCESS);
        checkoutButton.setAriaLabel(getTranslation(CHECKOUT_BUTTON__TOOLTIP));
        checkoutButton.setDisableOnClick(true);
        checkoutButton.setEnabled(false);
        checkoutButton.setVisible(false);
        checkoutButton.addClickListener(this::onClickCheckout);

        headerSection.add(header, itemCountSpan, checkoutButton);

        vendorContainer = new Div();
        vendorContainer.addClassNames(
                Display.BLOCK,
                Margin.Top.MEDIUM,
                Padding.NONE,
                Border.BOTTOM,
                BorderColor.CONTRAST_10);
        vendorContainer.setWidth(72, Unit.EX);

        add(headerSection);

        this.confirmationDialog.addCheckoutConfirmedListener(this::onCheckoutConfirmedEvent);
        this.confirmationDialog.addOpenedChangeListener(
                event -> {
                    if (!event.isOpened()) {
                        checkoutButton.setEnabled(true);
                    }
                });
        add(this.confirmationDialog);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);

        String headerTitle = getTranslation(TITLE);
        header.setText(headerTitle);

        updateSum();
    }

    public void addItem(DataModel.Vendor.Key vendor, BigDecimal price) {
        if (this.getChildren().noneMatch(component -> component.equals(vendorContainer))) {
            add(vendorContainer);
        }

        PurchaseVendorDetails vendorDetails =
                findVendorDetails(vendor).orElseGet(() -> createVendorDetails(vendor));
        vendorDetails.removeFromParent();
        vendorContainer.addComponentAsFirst(vendorDetails);
        vendorDetails.addItem(price);
    }

    public void clear() {
        clearItems();
    }

    public long itemCount() {
        return getAllItems().count();
    }

    public void addSaveCheckoutEventListener(ComponentEventListener<SaveCheckoutEvent> listener) {
        addListener(SaveCheckoutEvent.class, listener);
    }

    private Stream<DataModel.PurchaseItem> getAllItems() {
        return getVendorPanels().flatMap(PurchaseVendorDetails::getItems);
    }

    private void clearItems() {
        this.vendorContainer.getChildren().forEach(Component::removeFromParent);
        updateState();
    }

    private Stream<PurchaseVendorDetails> getVendorPanels() {
        return vendorContainer.getChildren().map(panel -> (PurchaseVendorDetails) panel);
    }

    private Optional<PurchaseVendorDetails> findVendorDetails(DataModel.Vendor.Key vendor) {
        return getVendorPanels()
                .filter(panel -> Objects.equals(panel.getVendor(), vendor))
                .findAny();
    }

    private PurchaseVendorDetails createVendorDetails(DataModel.Vendor.Key vendor) {
        PurchaseVendorDetails vendorPanel = new PurchaseVendorDetails(vendor);
        vendorPanel.addItemsChangedListener(this::onVendorItemsChangedListener);
        return vendorPanel;
    }

    private void updateState() {
        boolean empty = vendorContainer.getChildren().findAny().isEmpty();
        checkoutButton.setEnabled(!empty);
        checkoutButton.setVisible(!empty);

        updateSum();

        Optional<PurchaseVendorDetails> anyVendor = getVendorPanels().findAny();
        if (anyVendor.isEmpty()) {
            remove(vendorContainer);
        }

        long itemCount = getAllItems().count();
        if (itemCount > 0) {
            itemCountSpan.setText(getTranslation(ITEM_COUNT__TEXT, itemCount));
        } else {
            itemCountSpan.setText("");
        }
    }

    private void updateSum() {
        double purchaseSum =
                getVendorPanels().mapToDouble(PurchaseVendorDetails::getSumOfItems).sum();
        I18N.LocaleFormat format = I18N.i18N().format(getLocale());
        String purchaseSumText = format.currency(purchaseSum);
        purchaseSumSpan.setText(purchaseSumText);
        String tooltipText;
        if (purchaseSum == 0d) {
            tooltipText = getTranslation(CHECKOUT_BUTTON__TEXT);
            checkoutButton.setText("");
        } else {
            tooltipText = getTranslation(CHECKOUT_BUTTON__TEXT_WITH_VALUE, purchaseSumText);
            checkoutButton.setText(purchaseSumText);
        }
        Tooltip.forComponent(checkoutButton).setText(tooltipText);
    }

    private void completeCheckout(ServiceModel.Checkout checkout) {
        fireEvent(new SaveCheckoutEvent(this, false, checkout));
        clear();
    }

    private void onVendorItemsChangedListener(PurchaseVendorDetails.ItemsChangedEvent event) {
        updateState();
    }

    private void onClickCheckout(ClickEvent<Button> event) {
        List<DataModel.PurchaseItem> itemList = getAllItems().collect(Collectors.toList());
        if (itemList.isEmpty()) {
            return;
        }

        DataModel.Booth.Key purchaseBooth = itemList.getFirst().key().purchase().booth();
        ServiceModel.Checkout checkout =
                ServiceModel.Checkout.builder().booth(purchaseBooth).items(itemList).build();
        if (config.confirmationRequired()) {
            confirmationDialog.open(checkout);
        } else {
            completeCheckout(checkout);
        }
    }

    private void onCheckoutConfirmedEvent(CheckoutConfirmedEvent event) {
        completeCheckout(event.getCheckout());
    }

    @Getter
    public static class SaveCheckoutEvent extends ComponentEvent<PurchaseSummary> {
        private final ServiceModel.Checkout checkout;

        public SaveCheckoutEvent(
                PurchaseSummary source, boolean fromClient, ServiceModel.Checkout checkout) {
            super(source, fromClient);
            this.checkout = checkout;
        }
    }
}
