/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.ui.views;

import static com.vaadin.flow.data.value.ValueChangeMode.TIMEOUT;
import static com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import static org.apache.commons.lang3.StringUtils.containsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.stripToNull;
import static org.vaadin.lineawesome.LineAwesomeIcon.SEARCH_SOLID;
import static tschuba.ez.booth.i18n.TranslationKeys.VendorReportView.BUTTON_PRINT_ALL_RECEIPTS__TOOLTIP;
import static tschuba.ez.booth.i18n.TranslationKeys.VendorReportView.TITLE;

import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.virtuallist.VirtualList;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.lineawesome.LineAwesomeIcon;
import tschuba.ez.booth.data.PurchaseItemRepository;
import tschuba.ez.booth.data.VendorRepository;
import tschuba.ez.booth.model.DataModel;
import tschuba.ez.booth.model.EntitiesMapper;
import tschuba.ez.booth.model.EntityModel;
import tschuba.ez.booth.reporting.ReportingException;
import tschuba.ez.booth.services.ReportingService;
import tschuba.ez.booth.services.ServiceModel;
import tschuba.ez.booth.ui.components.ItemListItem;
import tschuba.ez.booth.ui.components.VendorCard;
import tschuba.ez.booth.ui.components.VendorReportCard;
import tschuba.ez.booth.ui.components.event.BoothSelection;
import tschuba.ez.booth.ui.components.event.EventRequired;
import tschuba.ez.booth.ui.components.model.ItemComparator;
import tschuba.ez.booth.i18n.TranslationKeys;
import tschuba.ez.booth.ui.layouts.OneColumnLayout;
import tschuba.ez.booth.ui.layouts.app.AppLayoutWithMenu;
import tschuba.ez.booth.ui.util.CssUnit;
import tschuba.ez.booth.ui.util.NavigateTo;
import tschuba.ez.booth.ui.util.Notifications;
import tschuba.ez.booth.ui.util.ReportViewHelper;

@Route(value = "reports/vendor", layout = AppLayoutWithMenu.class)
@EventRequired
public class VendorReportView extends OneColumnLayout {
    private static final Logger LOGGER = LoggerFactory.getLogger(VendorReportView.class);

    private static final Unit CSS_UNIT = Unit.EM;
    private static final CssUnit MIN_HEIGHT = CssUnit.cssUnit(40, CSS_UNIT);

    private final VendorRepository vendors;
    private final PurchaseItemRepository items;
    private final ReportingService reportingService;
    private final ReportViewHelper helper;

    private final VirtualList<ServiceModel.VendorReportData> vendorList;
    private final VirtualList<DataModel.PurchaseItem> itemList;
    private final Button printAllButton;
    private final TextField filterField;
    private VendorCard selectedItem;
    private String filterText;

    public VendorReportView(
            @NonNull VendorRepository vendors,
            @NonNull PurchaseItemRepository items,
            @NonNull ReportingService reportingService,
            @NonNull ReportViewHelper helper) {
        this.vendors = vendors;
        this.items = items;
        this.reportingService = reportingService;
        this.helper = helper;

        filterField = new TextField();
        filterField.setClearButtonVisible(true);
        filterField.setSuffixComponent(SEARCH_SOLID.create());
        filterField.setValueChangeMode(TIMEOUT);
        filterField.addValueChangeListener(this::onSearchFieldValueChange);
        filterField.setWidth(20, CSS_UNIT);

        printAllButton = new Button(LineAwesomeIcon.STACK_OVERFLOW.create());
        printAllButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_PRIMARY);
        printAllButton.addClassNames(Margin.Left.LARGE);
        printAllButton.addClickListener(this::onClickPrintAll);

        HorizontalLayout topBar = new HorizontalLayout();
        topBar.addClassNames(Padding.Left.SMALL, Padding.Bottom.MEDIUM);
        topBar.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        topBar.setDefaultVerticalComponentAlignment(Alignment.CENTER);
        topBar.add(filterField, printAllButton);

        vendorList = new VirtualList<>();
        vendorList.setMinWidth(27, CSS_UNIT);
        vendorList.setMinHeight(MIN_HEIGHT.getValue(), MIN_HEIGHT.getUnit());

        itemList = new VirtualList<>();
        itemList.setMinWidth(18, CSS_UNIT);
        itemList.setMinHeight(MIN_HEIGHT.getValue(), MIN_HEIGHT.getUnit());

        HorizontalLayout listsContainer = new HorizontalLayout(vendorList, itemList);
        VerticalLayout content = new VerticalLayout(topBar, listsContainer);
        setContent(content);
    }

    @Override
    protected void onAttach(AttachEvent event) {
        super.onAttach(event);

        setTitle(getTranslation(TITLE));

        filterField.setPlaceholder(
                getTranslation(TranslationKeys.VendorReportView.FILTER_FIELD__PLACEHOLDER));

        Tooltip.forComponent(printAllButton)
                .withText(getTranslation(BUTTON_PRINT_ALL_RECEIPTS__TOOLTIP));

        vendorList.setRenderer(
                new ComponentRenderer<>(
                        vendorData -> {
                            VendorReportCard vendorReportCard = new VendorReportCard(vendorData);
                            vendorReportCard.addClickListener(this::onClickVendorCard);
                            return vendorReportCard;
                        }));

        itemList.setRenderer(
                new ComponentRenderer<>(
                        item -> {
                            ItemListItem listItem = new ItemListItem(item);
                            listItem.addClassNames(Padding.Top.XSMALL, Padding.Bottom.XSMALL);
                            return listItem;
                        }));

        updateVendorListItems();
        updateItemListItems();
    }

    private void updateVendorListItems() {
        BoothSelection.get()
                .ifPresent(
                        booth -> {
                            Stream<DataModel.Vendor> allVendors =
                                    vendors.findAllByBoothId(booth.boothId()).stream()
                                            .map(EntitiesMapper::entityToObject)
                                            .filter(
                                                    vendor ->
                                                            Optional.ofNullable(filterText)
                                                                    .map(
                                                                            filter ->
                                                                                    containsIgnoreCase(
                                                                                            vendor.key()
                                                                                                    .vendorId(),
                                                                                            filter))
                                                                    .orElse(true));
                            Stream<ServiceModel.VendorReportData> vendorData =
                                    allVendors
                                            .parallel()
                                            .map(
                                                    vendor -> {
                                                        try {
                                                            return reportingService
                                                                    .createVendorReportData(
                                                                            vendor.key());
                                                        } catch (ReportingException ex) {
                                                            throw new RuntimeException(ex);
                                                        }
                                                    });
                            vendorList.setItems(vendorData);
                        });
    }

    private void updateItemListItems() {
        Stream<DataModel.PurchaseItem> itemsOfReport;
        try {
            if (selectedItem != null) {
                DataModel.Vendor vendor = selectedItem.getVendor();
                ItemComparator comparator =
                        ItemComparator.builder()
                                .descending(ItemComparator.Field.DateTime)
                                .ascending(ItemComparator.Field.Price)
                                .build();
                ServiceModel.VendorReportData reportData =
                        reportingService.createVendorReportData(vendor.key());
                List<EntityModel.PurchaseItem.Key> itemIds =
                        reportData.items().stream()
                                .map(DataModel.PurchaseItem::key)
                                .map(EntitiesMapper::objectToEntity)
                                .toList();
                itemsOfReport =
                        items.findAllById(itemIds).stream()
                                .map(EntitiesMapper::entityToObject)
                                .sorted(comparator);
            } else {
                itemsOfReport = Stream.empty();
            }
            itemList.setItems(itemsOfReport);
        } catch (ReportingException ex) {
            LOGGER.error("Failed to update item-list items!", ex);
            Notifications.error(
                    getTranslation(TranslationKeys.Notifications.GENERIC_ERROR_MESSAGE), ex);
        }
    }

    private void onSearchFieldValueChange(ComponentValueChangeEvent<TextField, String> event) {
        filterText = stripToNull(event.getValue());
        if (!Objects.equals(event.getValue(), event.getOldValue())) {
            updateVendorListItems();
        }
    }

    private void onClickVendorCard(ClickEvent<Div> event) {
        if (selectedItem != null) {
            selectedItem.unselect();
        }
        selectedItem = (VendorCard) event.getSource();
        updateItemListItems();
        selectedItem.select();
    }

    private void onClickPrintAll(ClickEvent<Button> buttonClickEvent) {
        BoothSelection.get()
                .ifPresent(
                        booth -> {
                            DataModel.Vendor.Key[] allVendors =
                                    vendors.findAllByBoothId(booth.boothId()).stream()
                                            .map(EntityModel.Vendor::getKey)
                                            .map(EntitiesMapper::entityToObject)
                                            .toArray(DataModel.Vendor.Key[]::new);
                            try {
                                URI reportFile = reportingService.generateVendorReport(allVendors);
                                URI reportUrl = helper.reportUrl(reportFile);
                                NavigateTo.uri(reportUrl).newWindow();
                            } catch (ReportingException ex) {
                                Notifications.error(ex.getLocalizedMessage(), ex);
                            }
                        });
    }
}
