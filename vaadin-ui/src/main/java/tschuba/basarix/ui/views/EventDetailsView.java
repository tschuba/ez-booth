package tschuba.basarix.ui.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import org.vaadin.lineawesome.LineAwesomeIcon;
import tschuba.basarix.data.model.Event;
import tschuba.basarix.data.model.EventKey;
import tschuba.basarix.data.model.Vendor;
import tschuba.basarix.data.model.VendorKey;
import tschuba.basarix.reporting.ReportingException;
import tschuba.basarix.reporting.SalesReportData;
import tschuba.basarix.reporting.VendorReportingService;
import tschuba.basarix.services.EventService;
import tschuba.basarix.services.VendorService;
import tschuba.basarix.services.dto.CloseEventException;
import tschuba.basarix.services.dto.DeleteEventException;
import tschuba.basarix.services.dto.OpenEventException;
import tschuba.basarix.ui.components.Block;
import tschuba.basarix.ui.components.ConfirmativeButton;
import tschuba.basarix.ui.components.event.EventSavedEvent;
import tschuba.basarix.ui.components.event.EventSelection;
import tschuba.basarix.ui.components.event.UpsertEventDialog;
import tschuba.basarix.ui.i18n.TranslationKeys;
import tschuba.basarix.ui.layouts.TwoColumnLayout;
import tschuba.basarix.ui.layouts.app.AppLayoutWithMenu;
import tschuba.basarix.ui.util.RoutingParameters;
import tschuba.commons.vaadin.NavigateTo;
import tschuba.commons.vaadin.Notifications;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static com.vaadin.flow.component.button.ButtonVariant.*;
import static java.util.Optional.empty;
import static tschuba.basarix.ui.i18n.TranslationKeys.EventDetailsView.*;
import static tschuba.commons.vaadin.components.Buttons.enableAfterClick;
import static tschuba.commons.vaadin.i18n.Formats.formats;

@Route(value = "event/:eventId", layout = AppLayoutWithMenu.class)
public class EventDetailsView extends TwoColumnLayout implements BeforeEnterObserver {
    private final EventService eventService;
    private final VendorService vendorService;
    private final VendorReportingService reportingService;

    private final AtomicReference<Optional<Event>> eventRef = new AtomicReference<>(empty());

    private final Block description;
    private final Block date;
    private final Block participationFee;
    private final Block salesFee;
    private final Block feesRoundingStep;
    private final Block totalVendorCount;
    private final Block totalItemCount;
    private final Block totalItemSum;
    private final Block totalParticipationFee;
    private final Block totalSalesFee;
    private final Block totalRevenue;

    private final UpsertEventDialog editDialog;

    private final Button editButton;
    private final Button closeButton;
    private final Button openButton;
    private final ConfirmativeButton deleteButton;

    public EventDetailsView(final EventService eventService,
                            final VendorService vendorService,
                            final VendorReportingService reportingService) {
        this.eventService = eventService;
        this.vendorService = vendorService;
        this.reportingService = reportingService;

        editDialog = new UpsertEventDialog(eventService);
        editDialog.addEventSavedListener(this::onEventSaved);

        editButton = new Button(LineAwesomeIcon.EDIT.create());
        editButton.addThemeVariants(LUMO_TERTIARY);
        editButton.addClickListener(clickEvent -> {
            Event eventToEdit = eventRef.get().orElseThrow();
            editDialog.open(eventToEdit);
        });

        closeButton = new Button(LineAwesomeIcon.LOCK_SOLID.create());
        closeButton.addThemeVariants(LUMO_TERTIARY);
        closeButton.setDisableOnClick(true);
        closeButton.addClickListener(enableAfterClick(clickEvent -> {
            try {
                EventKey eventToClose = eventRef.get().map(Event::getKey).orElseThrow();
                Event closedEvent = eventService.closeEvent(eventToClose);
                updateView(closedEvent);
            } catch (CloseEventException ex) {
                Notifications.error(CLOSE_EVENT_FAILED__MESSAGE, ex);
            }
        }));
        Tooltip.forComponent(closeButton).setText(getTranslation(CLOSE_BUTTON__TEXT));

        openButton = new Button(LineAwesomeIcon.LOCK_OPEN_SOLID.create());
        openButton.addThemeVariants(LUMO_TERTIARY);
        openButton.setDisableOnClick(true);
        openButton.addClickListener(enableAfterClick(clickEvent -> {
            try {
                EventKey eventToOpen = eventRef.get().map(Event::getKey).orElseThrow();
                Event openedEvent = eventService.openEvent(eventToOpen);
                updateView(openedEvent);
            } catch (OpenEventException ex) {
                Notifications.error(OPEN_EVENT_FAILED__MESSAGE, ex);
            }
        }));
        Tooltip.forComponent(openButton).setText(getTranslation(OPEN_BUTTON__TEXT));

        deleteButton = new ConfirmativeButton(LineAwesomeIcon.TRASH_ALT_SOLID.create());
        deleteButton.addThemeVariants(LUMO_PRIMARY, LUMO_ERROR);
        deleteButton.addClassNames(Margin.Left.MEDIUM);
        deleteButton.addConfirmationListener(clickEvent -> {
            try {
                EventKey eventToDelete = eventRef.get().map(Event::getKey).orElseThrow();
                eventService.deleteEvent(eventToDelete);
                EventSelection.deleted(eventToDelete);
                NavigateTo.view(EventSelectionView.class).currentWindow();
            } catch (DeleteEventException ex) {
                Notifications.error(DELETE_EVENT_FAILED__MESSAGE, ex);
            }
        });

        HorizontalLayout actionBar = new HorizontalLayout(Alignment.CENTER, editButton, closeButton, openButton, deleteButton);
        actionBar.setSpacing(false);
        actionBar.addClassNames(Padding.Top.SMALL, Padding.Bottom.LARGE);
        setTitle(getTranslation(TITLE), actionBar);

        Div leftColumn = new Div();
        setLeftColumn(leftColumn);

        Div rightColumn = new Div();
        setRightColumn(rightColumn);

        description = createBlock(DESCRIPTION__LABEL);
        date = createBlock(DATE__LABEL);
        participationFee = createBlock(PARTICIPATION_FEE__LABEL);
        salesFee = createBlock(SALES_FEE__LABEL);
        feesRoundingStep = createBlock(FEES_ROUNDING_STEP__LABEL);

        leftColumn.add(description, date, participationFee, salesFee, feesRoundingStep);
        leftColumn.add(editDialog);

        totalVendorCount = createBlock(TOTAL_VENDOR_COUNT__LABEL);
        totalItemCount = createBlock(TOTAL_ITEM_COUNT__LABEL);
        totalItemSum = createBlock(TOTAL_ITEM_SUM__LABEL);
        totalParticipationFee = createBlock(TOTAL_PARTICIPATION_FEE__LABEL);
        totalSalesFee = createBlock(TOTAL_SALES_FEE__LABEL);
        totalRevenue = createBlock(TOTAL_REVENUE__LABEL);

        rightColumn.add(totalVendorCount, totalItemCount, totalItemSum,
                totalParticipationFee, totalRevenue);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent viewEvent) {
        Optional<String> eventId = RoutingParameters.parser(viewEvent.getRouteParameters()).eventId();
        if (eventId.isEmpty()) {
            String message = getTranslation(TranslationKeys.EventDetailsView.NOTIFICATION__ILLEGAL_ARGUMENTS);
            Notifications.error(message);
            return;
        }

        Optional<Event> eventData = eventService.byKey(EventKey.of(eventId.get()));
        updateView(eventData.orElseThrow());
    }

    private void onEventSaved(EventSavedEvent eventSavedEvent) {
        updateView(eventSavedEvent.getEvent());
    }

    private void updateView(final Event event) {
        this.eventRef.set(Optional.of(event));
        updateView();
    }

    private void updateView() {
        final Event event = eventRef.get().orElseThrow();
        List<VendorKey> allVendors = vendorService.allVendors(event.getKey()).map(Vendor::getKey).toList();
        SalesReportData salesData = allVendors.stream().parallel()
                .map(vendor -> {
                    try {
                        return reportingService.basicReportData(vendor);
                    } catch (ReportingException ex) {
                        throw new RuntimeException("Failed to generate report data for vendor %s".formatted(vendor), ex);
                    }
                })
                .reduce(new SalesReportData(), SalesReportData::add, SalesReportData::add);

        Locale locale = getLocale();
        description.setContent(event.getDescription());
        date.setContent(formats().date(event.getDate(), locale));
        participationFee.setContent(formats().currency(event.getParticipationFee(), locale));
        salesFee.setContent(String.format("%s %%", formats().decimalNumber(event.getSalesFee(), locale)));
        feesRoundingStep.setContent(formats().currency(event.getFeesRoundingStep(), locale));

        totalVendorCount.setContent(Integer.toString(allVendors.size()));
        totalItemCount.setContent(Long.toString(salesData.itemCount()));
        totalItemSum.setContent(formats().currency(salesData.salesSum(), locale));
        totalParticipationFee.setContent(formats().currency(salesData.participationFee(), locale));
        totalSalesFee.setContent(formats().currency(salesData.salesFee(), locale));
        totalRevenue.setContent(formats().currency(salesData.totalRevenue(), locale));

        editButton.setEnabled(!event.isClosed());
        String editButtonText = getTranslation(event.isClosed() ? EDIT_BUTTON_DISABLED__TEXT : EDIT_BUTTON__TEXT);
        Tooltip.forComponent(editButton).setText(getTranslation(editButtonText));

        closeButton.setVisible(!event.isClosed());

        openButton.setVisible(event.isClosed());

        deleteButton.setEnabled(event.isClosed());
        String deleteButtonText = getTranslation((event.isClosed()) ? DELETE_BUTTON__TEXT : DELETE_BUTTON_DISABLED__TEXT);
        Tooltip.forComponent(deleteButton).setText(deleteButtonText);
    }

    private Block createBlock(String titleKey) {
        Block block = new Block();
        block.setTitle(getTranslation(titleKey));
        return block;
    }
}
