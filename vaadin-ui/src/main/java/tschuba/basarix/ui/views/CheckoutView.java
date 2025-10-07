/* Licensed under MIT

Copyright (c) 2025 Thomas Schulte-Bahrenberg */
package tschuba.basarix.ui.views;

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
import org.vaadin.lineawesome.LineAwesomeIcon;
import tschuba.basarix.data.model.EventKey;
import tschuba.basarix.data.model.Purchase;
import tschuba.basarix.data.model.VendorKey;
import tschuba.basarix.services.CheckoutService;
import tschuba.basarix.services.EventService;
import tschuba.basarix.services.dto.Checkout;
import tschuba.basarix.ui.components.checkout.CheckoutItemForm;
import tschuba.basarix.ui.components.checkout.CheckoutKeyPad;
import tschuba.basarix.ui.components.checkout.PurchaseSummary;
import tschuba.basarix.ui.components.event.EventRequired;
import tschuba.basarix.ui.components.event.EventSelection;
import tschuba.basarix.ui.components.model.PurchaseComparator;
import tschuba.basarix.ui.components.model.PurchaseGrid;
import tschuba.basarix.ui.layouts.TwoColumnLayout;
import tschuba.basarix.ui.layouts.app.AppLayoutWithMenu;
import tschuba.basarix.ui.renderer.ColumnRenderer;
import tschuba.basarix.ui.util.BadgeBuilder;
import tschuba.basarix.ui.util.RoutingParameters;
import tschuba.commons.vaadin.NavigateTo;
import tschuba.commons.vaadin.Notifications;

import java.util.List;
import java.util.Optional;

import static com.vaadin.flow.theme.lumo.LumoUtility.Display;
import static com.vaadin.flow.theme.lumo.LumoUtility.FlexDirection;
import static tschuba.basarix.ui.i18n.TranslationKeys.CheckoutView.*;
import static tschuba.commons.vaadin.i18n.Formats.formats;

@Route(value = "checkout", layout = AppLayoutWithMenu.class)
@PreserveOnRefresh
@EventRequired
public class CheckoutView extends TwoColumnLayout implements BeforeLeaveObserver {
    private final CheckoutService checkoutService;
    private final EventService eventService;

    private final PurchaseSummary purchaseSummary;
    private final CheckoutItemForm checkoutForm;
    private final PurchaseGrid<Void> purchaseGrid;
    private final CheckoutKeyPad numPad;

    public CheckoutView(final EventService eventService, final CheckoutService checkoutService, final PurchaseSummary purchaseSummary) {
        this.eventService = eventService;
        this.checkoutService = checkoutService;

        this.purchaseSummary = purchaseSummary;

        setTitle(getTranslation(TITLE));

        Section checkoutSection = new Section();
        checkoutSection.addClassNames(Display.FLEX, FlexDirection.COLUMN);

        checkoutForm = createCheckoutItemForm();
        checkoutSection.add(checkoutForm);

        purchaseSummary.addSaveCheckoutEventListener(this::onSaveCheckoutEvent);
        checkoutSection.add(purchaseSummary);

        purchaseGrid = createPurchaseGrid(eventService);
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

        EventSelection.get().flatMap(eventService::byKey).ifPresent(event -> {
            checkoutForm.setEnabled(!event.isClosed());
            numPad.setEnabled(!event.isClosed());
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

    private PurchaseGrid<Void> createPurchaseGrid(EventService eventService) {
        PurchaseComparator dateTimeComparator = PurchaseComparator.builder().ascending(PurchaseComparator.Field.DateTime).build();
        PurchaseGrid<Void> purchaseGrid = new PurchaseGrid<>(eventService);
        purchaseGrid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_COMPACT);
        purchaseGrid.setMinHeight(40, Unit.EM);
        purchaseGrid.setWidth("31rem");
        Column<Purchase> dateTimeColumn = purchaseGrid.addColumn(ColumnRenderer.Purchase.dateTime(formats(), getLocale())).setComparator(dateTimeComparator).setHeader(getTranslation(PURCHASE_GRID__HEADER__DATE_TIME)).setWidth("11rem").setFlexGrow(0);
        purchaseGrid.addColumn(ColumnRenderer.Purchase.sum(formats(), getLocale())).setHeader(getTranslation(PURCHASE_GRID__HEADER__VALUE)).setWidth("6rem").setFlexGrow(0);
        purchaseGrid.addColumn(new ComponentRenderer<>(purchase -> {
            String purchaseId = purchase.getKey().getId();
            String purchaseIdShort = "%s...".formatted(purchaseId.split("-")[0]);
            Span idBadge = BadgeBuilder.badge().apply(new Span(purchaseIdShort));
            Tooltip.forComponent(idBadge).setText(purchaseId);
            return idBadge;
        })).setHeader(getTranslation(PURCHASE_GRID__HEADER__ID)).setWidth("8rem").setFlexGrow(0);
        purchaseGrid.addColumn(new ComponentRenderer<>(purchase -> {
            Button printReceiptButton = new Button();
            printReceiptButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_LARGE, ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_CONTRAST);
            printReceiptButton.setIcon(LineAwesomeIcon.RECEIPT_SOLID.create());
            printReceiptButton.setTooltipText(getTranslation(BUTTON_PRINT_RECEIPT__TOOLTIP));
            printReceiptButton.addClickListener(event -> openPrintReceiptView(purchase, false));
            return printReceiptButton;
        })).setWidth("5rem").setFlexGrow(0);
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

    private void openPrintReceiptView(Purchase purchase, boolean showNotification) {
        RouteParameters parameters = RoutingParameters.builder().purchase(purchase.getKey()).build();
        NavigateTo.view(PurchaseReceiptPrintView.class, parameters).newWindow();
        if (showNotification) {
            Notifications.message(getTranslation(NOTIFICATION__PRINT_RECEIPT_TRIGGERED));
        }
    }

    private void onAddItemEvent(CheckoutItemForm.AddItemEvent event) {
        Optional<EventKey> currentEvent = EventSelection.get();
        if (currentEvent.isPresent()) {
            VendorKey vendor = VendorKey.of(currentEvent.get(), event.getVendorId());
            purchaseSummary.addItem(vendor, event.getPrice());
        }
    }

    private void onSaveCheckoutEvent(PurchaseSummary.SaveCheckoutEvent event) {
        try {
            Checkout checkout = event.getCheckout();
            Purchase purchase = checkoutService.save(checkout);
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
