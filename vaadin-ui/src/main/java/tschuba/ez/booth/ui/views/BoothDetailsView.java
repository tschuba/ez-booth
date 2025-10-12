/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.ui.views;

import static com.vaadin.flow.component.button.ButtonVariant.LUMO_ERROR;
import static com.vaadin.flow.component.button.ButtonVariant.LUMO_PRIMARY;
import static com.vaadin.flow.component.button.ButtonVariant.LUMO_TERTIARY;
import static java.util.Optional.empty;
import static tschuba.ez.booth.i18n.TranslationKeys.BoothDetailsView.CLOSE_BOOTH_FAILED__MESSAGE;
import static tschuba.ez.booth.i18n.TranslationKeys.BoothDetailsView.CLOSE_BUTTON__TEXT;
import static tschuba.ez.booth.i18n.TranslationKeys.BoothDetailsView.DATE__LABEL;
import static tschuba.ez.booth.i18n.TranslationKeys.BoothDetailsView.DELETE_BOOTH_FAILED__MESSAGE;
import static tschuba.ez.booth.i18n.TranslationKeys.BoothDetailsView.DELETE_BUTTON_DISABLED__TEXT;
import static tschuba.ez.booth.i18n.TranslationKeys.BoothDetailsView.DELETE_BUTTON__TEXT;
import static tschuba.ez.booth.i18n.TranslationKeys.BoothDetailsView.DESCRIPTION__LABEL;
import static tschuba.ez.booth.i18n.TranslationKeys.BoothDetailsView.EDIT_BUTTON_DISABLED__TEXT;
import static tschuba.ez.booth.i18n.TranslationKeys.BoothDetailsView.EDIT_BUTTON__TEXT;
import static tschuba.ez.booth.i18n.TranslationKeys.BoothDetailsView.FEES_ROUNDING_STEP__LABEL;
import static tschuba.ez.booth.i18n.TranslationKeys.BoothDetailsView.OPEN_BOOTH_FAILED__MESSAGE;
import static tschuba.ez.booth.i18n.TranslationKeys.BoothDetailsView.OPEN_BUTTON__TEXT;
import static tschuba.ez.booth.i18n.TranslationKeys.BoothDetailsView.PARTICIPATION_FEE__LABEL;
import static tschuba.ez.booth.i18n.TranslationKeys.BoothDetailsView.SALES_FEE__LABEL;
import static tschuba.ez.booth.i18n.TranslationKeys.BoothDetailsView.TITLE;
import static tschuba.ez.booth.i18n.TranslationKeys.BoothDetailsView.TOTAL_ITEM_COUNT__LABEL;
import static tschuba.ez.booth.i18n.TranslationKeys.BoothDetailsView.TOTAL_ITEM_SUM__LABEL;
import static tschuba.ez.booth.i18n.TranslationKeys.BoothDetailsView.TOTAL_PARTICIPATION_FEE__LABEL;
import static tschuba.ez.booth.i18n.TranslationKeys.BoothDetailsView.TOTAL_PAYOUT__LABEL;
import static tschuba.ez.booth.i18n.TranslationKeys.BoothDetailsView.TOTAL_PURCHASE_COUNT__LABEL;
import static tschuba.ez.booth.i18n.TranslationKeys.BoothDetailsView.TOTAL_REVENUE__LABEL;
import static tschuba.ez.booth.i18n.TranslationKeys.BoothDetailsView.TOTAL_SALES_FEE__LABEL;
import static tschuba.ez.booth.i18n.TranslationKeys.BoothDetailsView.TOTAL_VENDOR_COUNT__LABEL;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.card.Card;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.theme.lumo.LumoUtility.FontSize;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import lombok.NonNull;
import org.vaadin.lineawesome.LineAwesomeIcon;
import tschuba.ez.booth.i18n.I18N;
import tschuba.ez.booth.i18n.TranslationKeys;
import tschuba.ez.booth.model.DataModel;
import tschuba.ez.booth.services.BoothService;
import tschuba.ez.booth.services.PurchaseService;
import tschuba.ez.booth.services.ReportingException;
import tschuba.ez.booth.services.ReportingService;
import tschuba.ez.booth.services.VendorService;
import tschuba.ez.booth.ui.components.ConfirmativeButton;
import tschuba.ez.booth.ui.components.event.BoothSavedEvent;
import tschuba.ez.booth.ui.components.event.BoothSelection;
import tschuba.ez.booth.ui.components.event.UpsertEventDialog;
import tschuba.ez.booth.ui.layouts.OneColumnLayout;
import tschuba.ez.booth.ui.layouts.app.AppLayoutWithMenu;
import tschuba.ez.booth.ui.util.Buttons;
import tschuba.ez.booth.ui.util.NavigateTo;
import tschuba.ez.booth.ui.util.Notifications;
import tschuba.ez.booth.ui.util.Routing;
import tschuba.ez.booth.ui.util.Spacing;

@Route(
    value = "booth/:" + Routing.Parameters.ROUTE_PARAM__BOOTH_ID,
    layout = AppLayoutWithMenu.class)
@SpringComponent
@UIScope
public class BoothDetailsView extends OneColumnLayout implements BeforeEnterObserver {
  private final BoothService boothRepo;
  private final VendorService vendorRepo;
  private final PurchaseService purchaseRepo;
  private final ReportingService reportingService;

  private final AtomicReference<Optional<DataModel.Booth>> boothRef =
      new AtomicReference<>(empty());

  private final Card description = new Card();
  private final Card date = new Card();
  private final Card participationFee = new Card();
  private final Card salesFee = new Card();
  private final Card feesRoundingStep = new Card();
  private final Card totalVendorCount = new Card();
  private final Card totalPurchaseCount = new Card();
  private final Card totalItemCount = new Card();
  private final Card totalItemSum = new Card();
  private final Card totalParticipationFee = new Card();
  private final Card totalSalesFee = new Card();
  private final Card totalRevenue = new Card();
  private final Card totalPayout = new Card();

  private final UpsertEventDialog editDialog;

  private final Button editButton;
  private final Button closeButton;
  private final Button openButton;
  private final ConfirmativeButton deleteButton;

  public BoothDetailsView(
      @NonNull final BoothService booths,
      @NonNull final BoothService boothService,
      @NonNull final VendorService vendors,
      @NonNull PurchaseService purchase,
      @NonNull final ReportingService reportingService) {
    this.boothRepo = booths;
    this.vendorRepo = vendors;
    this.purchaseRepo = purchase;
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
                DataModel.Booth closedBooth = boothService.close(boothToClose);
                updateView(closedBooth);
              } catch (Exception ex) {
                Notifications.error(CLOSE_BOOTH_FAILED__MESSAGE, ex);
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
                DataModel.Booth openedBooth = boothService.open(boothToOpen);
                updateView(openedBooth);
              } catch (Exception ex) {
                Notifications.error(OPEN_BOOTH_FAILED__MESSAGE, ex);
              }
            }));
    Tooltip.forComponent(openButton).setText(getTranslation(OPEN_BUTTON__TEXT));

    deleteButton = new ConfirmativeButton(LineAwesomeIcon.TRASH_ALT_SOLID.create());
    deleteButton.addThemeVariants(LUMO_PRIMARY, LUMO_ERROR);
    deleteButton.addClassNames(Margin.Left.MEDIUM);
    deleteButton.addConfirmationListener(
        _ -> {
          try {
            DataModel.Booth.Key boothToDelete =
                boothRef.get().map(DataModel.Booth::key).orElseThrow();
            boothService.delete(boothToDelete);
            BoothSelection.deleted(boothToDelete);
            NavigateTo.view(BoothSelectionView.class).currentWindow();
          } catch (Exception ex) {
            Notifications.error(DELETE_BOOTH_FAILED__MESSAGE, ex);
          }
        });

    HorizontalLayout actionBar =
        new HorizontalLayout(Alignment.CENTER, editButton, closeButton, openButton, deleteButton);
    actionBar.setSpacing(false);
    actionBar.addClassNames(Padding.Top.SMALL, Padding.Bottom.LARGE);
    setTitle(getTranslation(TITLE), actionBar);

    VerticalLayout eventConfig = new VerticalLayout(Alignment.STRETCH);
    eventConfig.setJustifyContentMode(JustifyContentMode.BETWEEN);
    eventConfig.setPadding(false);
    eventConfig.setMargin(false);
    Spacing.spacing(eventConfig).xsmall();

    HorizontalLayout dateAndFees =
        new HorizontalLayout(Alignment.STRETCH, date, participationFee, salesFee, feesRoundingStep);
    dateAndFees.setJustifyContentMode(JustifyContentMode.BETWEEN);
    Spacing.spacing(dateAndFees).xsmall();

    eventConfig.add(description, dateAndFees);
    eventConfig.add(editDialog);

    VerticalLayout totalFees =
        new VerticalLayout(Alignment.STRETCH, totalParticipationFee, totalSalesFee);
    totalFees.setJustifyContentMode(JustifyContentMode.BETWEEN);
    totalFees.setPadding(false);
    totalFees.setMargin(false);
    Spacing.spacing(totalFees).xsmall();

    totalRevenue.add(totalFees);

    HorizontalLayout totalValues =
        new HorizontalLayout(Alignment.STRETCH, totalItemSum, totalRevenue, totalPayout);
    totalValues.setJustifyContentMode(JustifyContentMode.BETWEEN);
    Spacing.spacing(totalValues).xsmall();

    totalPurchaseCount.setMedia(LineAwesomeIcon.SHOPPING_BAG_SOLID.create());
    totalVendorCount.setMedia(LineAwesomeIcon.USERS_SOLID.create());
    totalItemCount.setMedia(LineAwesomeIcon.GIFTS_SOLID.create());
    HorizontalLayout totalCounts =
        new HorizontalLayout(
            Alignment.STRETCH, totalPurchaseCount, totalVendorCount, totalItemCount);
    totalCounts.setJustifyContentMode(JustifyContentMode.BETWEEN);
    totalCounts.setWidthFull();
    Spacing.spacing(totalCounts).xsmall();

    VerticalLayout content = new VerticalLayout(eventConfig, totalValues, totalCounts);
    content.setPadding(false);
    content.setMargin(false);
    Spacing.spacing(content).xlarge();
    setContent(content);
  }

  @Override
  protected void onAttach(AttachEvent attachEvent) {
    description.setTitle(getTranslation(DESCRIPTION__LABEL));
    date.setTitle(getTranslation(DATE__LABEL));

    participationFee.setTitle(getTranslation(PARTICIPATION_FEE__LABEL));
    salesFee.setTitle(getTranslation(SALES_FEE__LABEL));
    feesRoundingStep.setTitle(getTranslation(FEES_ROUNDING_STEP__LABEL));

    totalVendorCount.setTitle(new Span(getTranslation(TOTAL_VENDOR_COUNT__LABEL)));
    totalItemCount.setTitle(new Span(getTranslation(TOTAL_ITEM_COUNT__LABEL)));
    totalPurchaseCount.setTitle(new Span(getTranslation(TOTAL_PURCHASE_COUNT__LABEL)));
    totalItemSum.setTitle(getTranslation(TOTAL_ITEM_SUM__LABEL));
    totalParticipationFee.setSubtitle(new Span(getTranslation(TOTAL_PARTICIPATION_FEE__LABEL)));
    totalSalesFee.setSubtitle(new Span(getTranslation(TOTAL_SALES_FEE__LABEL)));
    totalRevenue.setTitle(getTranslation(TOTAL_REVENUE__LABEL));
    totalPayout.setTitle(getTranslation(TOTAL_PAYOUT__LABEL));
  }

  @Override
  public void beforeEnter(BeforeEnterEvent viewEvent) {
    Optional<String> eventId = Routing.Parameters.parser(viewEvent.getRouteParameters()).boothId();
    if (eventId.isEmpty()) {
      String message =
          getTranslation(TranslationKeys.BoothDetailsView.NOTIFICATION__ILLEGAL_ARGUMENTS);
      Notifications.error(message);
      return;
    }

    Optional<DataModel.Booth> boothData =
        boothRepo.findById(DataModel.Booth.Key.builder().boothId(eventId.get()).build());
    updateView(boothData.orElseThrow());
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
        vendorRepo.findByBooth(booth.key()).map(DataModel.Vendor::key).toList();

    final AtomicInteger itemCount = new AtomicInteger(0);
    final AtomicReference<BigDecimal> aggregatedItemSum = new AtomicReference<>(BigDecimal.ZERO);
    final AtomicReference<BigDecimal> aggregatedParticipationFee =
        new AtomicReference<>(BigDecimal.ZERO);
    final AtomicReference<BigDecimal> aggregatedPayout = new AtomicReference<>(BigDecimal.ZERO);
    allVendors.parallelStream()
        .map(
            vendor -> {
              try {
                return reportingService.createVendorReportData(vendor);
              } catch (ReportingException ex) {
                throw new RuntimeException(
                    "Failed to generate report data for vendor %s".formatted(vendor), ex);
              }
            })
        .forEach(
            report -> {
              itemCount.addAndGet(report.items().size());
              aggregatedItemSum.accumulateAndGet(report.salesSum(), BigDecimal::add);
              aggregatedParticipationFee.accumulateAndGet(
                  report.participationFee(), BigDecimal::add);
              aggregatedPayout.accumulateAndGet(report.totalRevenue(), BigDecimal::add);
            });
    final BigDecimal aggregatedRevenue = aggregatedItemSum.get().subtract(aggregatedPayout.get());
    final BigDecimal aggregatedSalesFee =
        aggregatedRevenue.subtract(aggregatedParticipationFee.get()).max(BigDecimal.ZERO);

    long countOfPurchases = purchaseRepo.findByBooth(booth.key()).count();

    I18N.LocaleFormat format = I18N.format(getLocale());

    description.add(booth.description());
    date.setSubtitle(valueComponent(format.date(booth.date()), FontSize.LARGE));
    participationFee.setSubtitle(
        valueComponent(format.currency(booth.participationFee()), FontSize.LARGE));
    salesFee.setSubtitle(
        valueComponent("%s %%".formatted(format.decimalNumber(booth.salesFee())), FontSize.LARGE));
    feesRoundingStep.setSubtitle(
        valueComponent(format.currency(booth.feesRoundingStep()), FontSize.LARGE));

    totalVendorCount.setSubtitle(valueComponent(Integer.toString(allVendors.size())));
    totalPurchaseCount.setSubtitle(valueComponent(Long.toString(countOfPurchases)));
    totalItemCount.setSubtitle(valueComponent(Long.toString(itemCount.get())));

    totalItemSum.setSubtitle(valueComponent(format.currency(aggregatedItemSum.get())));
    totalParticipationFee.setHeaderSuffix(
        new Span(format.currency(aggregatedParticipationFee.get())));
    totalSalesFee.setHeaderSuffix(new Span(format.currency(aggregatedSalesFee)));
    totalRevenue.setSubtitle(valueComponent(format.currency(aggregatedRevenue)));

    totalPayout.setSubtitle(valueComponent(format.currency(aggregatedPayout.get())));

    editButton.setEnabled(!booth.closed());
    String editButtonText =
        getTranslation(booth.closed() ? EDIT_BUTTON_DISABLED__TEXT : EDIT_BUTTON__TEXT);
    Tooltip.forComponent(editButton).setText(getTranslation(editButtonText));

    closeButton.setVisible(!booth.closed());

    openButton.setVisible(booth.closed());

    deleteButton.setEnabled(booth.closed());
    String deleteButtonText =
        getTranslation((booth.closed()) ? DELETE_BUTTON__TEXT : DELETE_BUTTON_DISABLED__TEXT);
    Tooltip.forComponent(deleteButton).setText(deleteButtonText);
  }

  private static Component valueComponent(@NonNull final String value) {
    return valueComponent(value, FontSize.XXLARGE);
  }

  private static Component valueComponent(@NonNull final String value, @NonNull String fontSize) {
    Span span = new Span(value);
    span.addClassNames(fontSize, Padding.Top.MEDIUM);
    return span;
  }
}
