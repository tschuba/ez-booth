/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.ui.views;

import static com.vaadin.flow.theme.lumo.LumoUtility.Display;
import static com.vaadin.flow.theme.lumo.LumoUtility.FlexDirection;
import static tschuba.ez.booth.i18n.TranslationKeys.CheckoutView.*;

import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Section;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.*;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;
import org.vaadin.lineawesome.LineAwesomeIcon;
import tschuba.ez.booth.i18n.I18N;
import tschuba.ez.booth.model.DataModel;
import tschuba.ez.booth.services.BoothService;
import tschuba.ez.booth.services.PurchaseService;
import tschuba.ez.booth.services.ServiceModel;
import tschuba.ez.booth.ui.components.checkout.CheckoutItemForm;
import tschuba.ez.booth.ui.components.checkout.CheckoutKeyPad;
import tschuba.ez.booth.ui.components.checkout.PurchaseSummary;
import tschuba.ez.booth.ui.components.event.BoothSelection;
import tschuba.ez.booth.ui.components.event.EventRequired;
import tschuba.ez.booth.ui.components.model.PurchaseComparator;
import tschuba.ez.booth.ui.components.model.PurchaseGrid;
import tschuba.ez.booth.ui.layouts.TwoColumnLayout;
import tschuba.ez.booth.ui.layouts.app.AppLayoutWithMenu;
import tschuba.ez.booth.ui.renderer.ColumnRenderer;
import tschuba.ez.booth.ui.util.*;

@Route(value = "checkout", layout = AppLayoutWithMenu.class)
@PreserveOnRefresh
@EventRequired
public class CheckoutView extends TwoColumnLayout implements BeforeLeaveObserver {
    private final PurchaseService purchaseService;
    private final BoothService booths;

    private final PurchaseSummary purchaseSummary;
    private final CheckoutItemForm checkoutForm;
    private final PurchaseGrid<Void> purchaseGrid;
    private final CheckoutKeyPad numPad;

    public CheckoutView(
            @NonNull final BoothService booths,
            @NonNull final PurchaseService purchaseService,
            @NonNull final PurchaseSummary purchaseSummary) {
        this.booths = booths;
        this.purchaseService = purchaseService;

        this.purchaseSummary = purchaseSummary;

        setTitle(getTranslation(TITLE));

        Section checkoutSection = new Section();
        checkoutSection.addClassNames(Display.FLEX, FlexDirection.COLUMN);

        checkoutForm = createCheckoutItemForm();
        checkoutSection.add(checkoutForm);

        purchaseSummary.addSaveCheckoutEventListener(this::onSaveCheckoutEvent);
        checkoutSection.add(purchaseSummary);

        purchaseGrid = createPurchaseGrid(purchaseService);
        purchaseGrid.setVisible(false);

        numPad = new CheckoutKeyPad(checkoutForm);
        numPad.setVisible(true);

        Div rightContent = new Div(purchaseGrid, numPad);

        setContents(checkoutSection, rightContent);
    }

    public boolean togglePurchaseHistory() {
        boolean showHistory = !purchaseGrid.isVisible();
        purchaseGrid.setVisible(showHistory);
        numPad.setVisible(!showHistory);
        return showHistory;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent enterEvent) {
        super.beforeEnter(enterEvent);

        BoothSelection.get()
                .flatMap(booths::findById)
                .ifPresent(
                        booth -> {
                            boolean inputEnabled = !booth.closed();
                            checkoutForm.setEnabled(inputEnabled);
                            numPad.setEnabled(inputEnabled);
                        });
    }

    @Override
    public void beforeLeave(BeforeLeaveEvent event) {
        if (hasUnsavedChanges()) {
            event.postpone();
            Notifications.warning(getTranslation(NOTIFICATION__UNSAVED_CHANGES));
        }
    }

    private boolean hasUnsavedChanges() {
        return purchaseSummary.itemCount() > 0;
    }

    private PurchaseGrid<Void> createPurchaseGrid(PurchaseService purchaseService) {

        I18N.LocaleFormat format = I18N.format(getLocale());

        PurchaseComparator dateTimeComparator =
                PurchaseComparator.builder().ascending(PurchaseComparator.Field.DateTime).build();
        PurchaseGrid<Void> purchaseGrid = new PurchaseGrid<>(purchaseService);
        purchaseGrid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_COMPACT);
        purchaseGrid.setMinHeight(40, Unit.EM);
        purchaseGrid.setWidth("31rem");
        Column<DataModel.Purchase> dateTimeColumn =
                purchaseGrid
                        .addColumn(ColumnRenderer.Purchase.dateTime(format))
                        .setComparator(dateTimeComparator)
                        .setHeader(getTranslation(PURCHASE_GRID__HEADER__DATE_TIME))
                        .setWidth("11rem")
                        .setFlexGrow(0);
        purchaseGrid
                .addColumn(ColumnRenderer.Purchase.sum(format))
                .setHeader(getTranslation(PURCHASE_GRID__HEADER__VALUE))
                .setWidth("6rem")
                .setFlexGrow(0);
        purchaseGrid
                .addColumn(
                        new ComponentRenderer<>(
                                purchase -> {
                                    String purchaseId = purchase.key().purchaseId();
                                    String purchaseIdShort =
                                            "%s...".formatted(purchaseId.split("-")[0]);
                                    Span idBadge =
                                            Badges.badge().applyTo(new Span(purchaseIdShort));
                                    Tooltip.forComponent(idBadge).setText(purchaseId);
                                    return idBadge;
                                }))
                .setHeader(getTranslation(PURCHASE_GRID__HEADER__ID))
                .setWidth("8rem")
                .setFlexGrow(0);
        purchaseGrid
                .addColumn(
                        new ComponentRenderer<>(
                                purchase -> {
                                    Button printReceiptButton = new Button();
                                    printReceiptButton.addThemeVariants(
                                            ButtonVariant.LUMO_ICON,
                                            ButtonVariant.LUMO_LARGE,
                                            ButtonVariant.LUMO_PRIMARY,
                                            ButtonVariant.LUMO_CONTRAST);
                                    printReceiptButton.setIcon(
                                            LineAwesomeIcon.RECEIPT_SOLID.create());
                                    printReceiptButton.setTooltipText(
                                            getTranslation(BUTTON_PRINT_RECEIPT__TOOLTIP));
                                    printReceiptButton.addClickListener(
                                            event -> openPrintReceiptView(purchase, false));
                                    return printReceiptButton;
                                }))
                .setWidth("5rem")
                .setFlexGrow(0);
        purchaseGrid.sort(List.of(new GridSortOrder<>(dateTimeColumn, SortDirection.DESCENDING)));
        return purchaseGrid;
    }

    private CheckoutItemForm createCheckoutItemForm() {
        CheckoutItemForm checkoutItemForm = new CheckoutItemForm();
        checkoutItemForm.addAddItemEventListener(this::onAddItemEvent);
        return checkoutItemForm;
    }

    private void refresh() {
        purchaseGrid.getDataProvider().refreshAll();
    }

    private void refreshAndClear() {
        refresh();
        clear();
    }

    private void clear() {
        purchaseSummary.clear();
        checkoutForm.clear();
    }

    private void openPrintReceiptView(DataModel.Purchase purchase, boolean showNotification) {
        RouteParameters parameters = Routing.Parameters.builder().purchase(purchase.key()).build();
        NavigateTo.view(PurchaseReceiptPrintView.class, parameters).newWindow();
        if (showNotification) {
            Notifications.message(getTranslation(NOTIFICATION__PRINT_RECEIPT_TRIGGERED));
        }
    }

    private void onAddItemEvent(CheckoutItemForm.AddItemEvent event) {
        Optional<DataModel.Booth.Key> currentBooth = BoothSelection.get();
        if (currentBooth.isPresent()) {
            DataModel.Vendor.Key vendor =
                    DataModel.Vendor.Key.builder()
                            .booth(currentBooth.get())
                            .vendorId(event.getVendorId())
                            .build();
            purchaseSummary.addItem(vendor, event.getPrice());
        }
    }

    private void onSaveCheckoutEvent(PurchaseSummary.SaveCheckoutEvent event) {
        try {
            ServiceModel.Checkout checkout = event.getCheckout();
            DataModel.Purchase purchase = purchaseService.checkout(checkout);
            Notifications.success(getTranslation(NOTIFICATION__PURCHASE_SUBMITTED));
            refreshAndClear();
            if (checkout.printReceipt()) {
                openPrintReceiptView(purchase, true);
            }
        } catch (Exception ex) {
            Notifications.error(getTranslation(NOTIFICATION__PURCHASE_SUBMISSION_FAILED), ex);
        }
    }
}
