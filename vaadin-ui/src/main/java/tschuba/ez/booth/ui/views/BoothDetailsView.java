/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.ui.views;

import static com.vaadin.flow.component.button.ButtonVariant.*;
import static java.util.Optional.empty;
import static tschuba.ez.booth.ui.i18n.Formats.formats;
import static tschuba.ez.booth.ui.i18n.TranslationKeys.EventDetailsView.*;

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
import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import lombok.NonNull;
import org.vaadin.lineawesome.LineAwesomeIcon;
import tschuba.ez.booth.data.BoothRepository;
import tschuba.ez.booth.data.VendorRepository;
import tschuba.ez.booth.model.DataModel;
import tschuba.ez.booth.model.EntitiesMapper;
import tschuba.ez.booth.model.EntityModel;
import tschuba.ez.booth.reporting.ReportingException;
import tschuba.ez.booth.services.BoothService;
import tschuba.ez.booth.services.ReportingService;
import tschuba.ez.booth.ui.components.Block;
import tschuba.ez.booth.ui.components.ConfirmativeButton;
import tschuba.ez.booth.ui.components.event.BoothSavedEvent;
import tschuba.ez.booth.ui.components.event.BoothSelection;
import tschuba.ez.booth.ui.components.event.UpsertEventDialog;
import tschuba.ez.booth.ui.i18n.TranslationKeys;
import tschuba.ez.booth.ui.layouts.TwoColumnLayout;
import tschuba.ez.booth.ui.layouts.app.AppLayoutWithMenu;
import tschuba.ez.booth.ui.util.*;

@Route(value = "event/:eventId", layout = AppLayoutWithMenu.class)
public class BoothDetailsView extends TwoColumnLayout implements BeforeEnterObserver {
    private final BoothRepository booths;
    private final VendorRepository vendors;
    private final ReportingService reportingService;

    private final AtomicReference<Optional<DataModel.Booth>> boothRef =
            new AtomicReference<>(empty());

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

    public BoothDetailsView(
            @NonNull final BoothRepository booths,
            @NonNull final BoothService boothService,
            @NonNull final VendorRepository vendors,
            @NonNull final ReportingService reportingService) {
        this.booths = booths;
        this.vendors = vendors;
        this.reportingService = reportingService;

        editDialog = new UpsertEventDialog(booths);
        editDialog.addEventSavedListener(this::onEventSaved);

        editButton = new Button(LineAwesomeIcon.EDIT.create());
        editButton.addThemeVariants(LUMO_TERTIARY);
        editButton.addClickListener(
                _ -> {
                    DataModel.Booth boothToEdit = boothRef.get().orElseThrow();
                    editDialog.open(boothToEdit);
                });

        closeButton = new Button(LineAwesomeIcon.LOCK_SOLID.create());
        closeButton.addThemeVariants(LUMO_TERTIARY);
        closeButton.setDisableOnClick(true);
        closeButton.addClickListener(
                Buttons.enableAfterClick(
                        _ -> {
                            try {
                                DataModel.Booth.Key boothToClose =
                                        boothRef.get().map(DataModel.Booth::key).orElseThrow();
                                DataModel.Booth closedBooth = boothService.closeBooth(boothToClose);
                                updateView(closedBooth);
                            } catch (Exception ex) {
                                Notifications.error(CLOSE_EVENT_FAILED__MESSAGE, ex);
                            }
                        }));
        Tooltip.forComponent(closeButton).setText(getTranslation(CLOSE_BUTTON__TEXT));

        openButton = new Button(LineAwesomeIcon.LOCK_OPEN_SOLID.create());
        openButton.addThemeVariants(LUMO_TERTIARY);
        openButton.setDisableOnClick(true);
        openButton.addClickListener(
                Buttons.enableAfterClick(
                        _ -> {
                            try {
                                DataModel.Booth.Key boothToOpen =
                                        boothRef.get().map(DataModel.Booth::key).orElseThrow();
                                DataModel.Booth openedBooth = boothService.openBooth(boothToOpen);
                                updateView(openedBooth);
                            } catch (Exception ex) {
                                Notifications.error(OPEN_EVENT_FAILED__MESSAGE, ex);
                            }
                        }));
        Tooltip.forComponent(openButton).setText(getTranslation(OPEN_BUTTON__TEXT));

        deleteButton = new ConfirmativeButton(LineAwesomeIcon.TRASH_ALT_SOLID.create());
        deleteButton.addThemeVariants(LUMO_PRIMARY, LUMO_ERROR);
        deleteButton.addClassNames(Margin.Left.MEDIUM);
        deleteButton.addConfirmationListener(
                clickEvent -> {
                    try {
                        DataModel.Booth.Key boothToDelete =
                                boothRef.get().map(DataModel.Booth::key).orElseThrow();
                        boothService.deleteBooth(boothToDelete);
                        BoothSelection.deleted(boothToDelete);
                        NavigateTo.view(BoothSelectionView.class).currentWindow();
                    } catch (Exception ex) {
                        Notifications.error(DELETE_EVENT_FAILED__MESSAGE, ex);
                    }
                });

        HorizontalLayout actionBar =
                new HorizontalLayout(
                        Alignment.CENTER, editButton, closeButton, openButton, deleteButton);
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

        rightColumn.add(
                totalVendorCount,
                totalItemCount,
                totalItemSum,
                totalParticipationFee,
                totalRevenue);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent viewEvent) {
        Optional<String> eventId =
                Routing.Parameters.parser(viewEvent.getRouteParameters()).boothId();
        if (eventId.isEmpty()) {
            String message =
                    getTranslation(
                            TranslationKeys.EventDetailsView.NOTIFICATION__ILLEGAL_ARGUMENTS);
            Notifications.error(message);
            return;
        }

        Optional<EntityModel.Booth> boothData =
                booths.findById(EntityModel.Booth.Key.builder().boothId(eventId.get()).build());
        updateView(EntitiesMapper.entityToObject(boothData.orElseThrow()));
    }

    private void onEventSaved(BoothSavedEvent boothSavedEvent) {
        updateView(boothSavedEvent.getBooth());
    }

    private void updateView(final DataModel.Booth booth) {
        this.boothRef.set(Optional.of(booth));
        updateView();
    }

    private void updateView() {
        final DataModel.Booth booth = boothRef.get().orElseThrow();
        List<DataModel.Vendor.Key> allVendors =
                vendors.findAllByBoothId(booth.key().boothId()).stream()
                        .map(EntityModel.Vendor::getKey)
                        .map(EntitiesMapper::entityToObject)
                        .toList();

        final AtomicInteger itemCount = new AtomicInteger(0);
        final AtomicReference<BigDecimal> aggregatedItemSum =
                new AtomicReference<>(BigDecimal.ZERO);
        final AtomicReference<BigDecimal> aggregatedParticipationFee =
                new AtomicReference<>(BigDecimal.ZERO);
        final AtomicReference<BigDecimal> aggregatedSalesFee =
                new AtomicReference<>(BigDecimal.ZERO);
        final AtomicReference<BigDecimal> aggregatedRevenue =
                new AtomicReference<>(BigDecimal.ZERO);
        allVendors.parallelStream()
                .map(
                        vendor -> {
                            try {
                                return reportingService.createVendorReportData(vendor);
                            } catch (ReportingException ex) {
                                throw new RuntimeException(
                                        "Failed to generate report data for vendor %s"
                                                .formatted(vendor),
                                        ex);
                            }
                        })
                .forEach(
                        report -> {
                            itemCount.incrementAndGet();
                            aggregatedItemSum.accumulateAndGet(report.salesSum(), BigDecimal::add);
                            aggregatedParticipationFee.accumulateAndGet(
                                    report.participationFee(), BigDecimal::add);
                            aggregatedSalesFee.accumulateAndGet(report.salesFee(), BigDecimal::add);
                            aggregatedRevenue.accumulateAndGet(
                                    report.totalRevenue(), BigDecimal::add);
                        });

        Locale locale = getLocale();
        description.setContent(booth.description());
        date.setContent(formats().date(booth.date(), locale));
        participationFee.setContent(formats().currency(booth.participationFee(), locale));
        salesFee.setContent(
                String.format("%s %%", formats().decimalNumber(booth.salesFee(), locale)));
        feesRoundingStep.setContent(formats().currency(booth.feesRoundingStep(), locale));

        totalVendorCount.setContent(Integer.toString(allVendors.size()));
        totalItemCount.setContent(Long.toString(itemCount.get()));
        totalItemSum.setContent(formats().currency(aggregatedItemSum.get(), locale));
        totalParticipationFee.setContent(
                formats().currency(aggregatedParticipationFee.get(), locale));
        totalSalesFee.setContent(formats().currency(aggregatedSalesFee.get(), locale));
        totalRevenue.setContent(formats().currency(aggregatedRevenue.get(), locale));

        editButton.setEnabled(!booth.closed());
        String editButtonText =
                getTranslation(booth.closed() ? EDIT_BUTTON_DISABLED__TEXT : EDIT_BUTTON__TEXT);
        Tooltip.forComponent(editButton).setText(getTranslation(editButtonText));

        closeButton.setVisible(!booth.closed());

        openButton.setVisible(booth.closed());

        deleteButton.setEnabled(booth.closed());
        String deleteButtonText =
                getTranslation(
                        (booth.closed()) ? DELETE_BUTTON__TEXT : DELETE_BUTTON_DISABLED__TEXT);
        Tooltip.forComponent(deleteButton).setText(deleteButtonText);
    }

    private Block createBlock(String titleKey) {
        Block block = new Block();
        block.setTitle(getTranslation(titleKey));
        return block;
    }
}
