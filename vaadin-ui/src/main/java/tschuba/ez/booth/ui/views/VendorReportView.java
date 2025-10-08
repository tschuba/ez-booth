/* Licensed under MIT

Copyright (c) 2025 Thomas Schulte-Bahrenberg */
package tschuba.ez.booth.ui.views;

import static com.vaadin.flow.data.value.ValueChangeMode.TIMEOUT;
import static com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import static org.apache.commons.lang3.StringUtils.containsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.stripToNull;
import static org.vaadin.lineawesome.LineAwesomeIcon.SEARCH_SOLID;
import static tschuba.ez.booth.ui.i18n.TranslationKeys.VendorReportView.BUTTON_PRINT_ALL_RECEIPTS__TOOLTIP;
import static tschuba.ez.booth.ui.i18n.TranslationKeys.VendorReportView.TITLE;

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
import java.io.File;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.lineawesome.LineAwesomeIcon;
import tschuba.basarix.data.model.Item;
import tschuba.basarix.data.model.Vendor;
import tschuba.basarix.data.model.VendorKey;
import tschuba.basarix.reporting.ReportingException;
import tschuba.basarix.reporting.VendorReportData;
import tschuba.basarix.reporting.VendorReportingService;
import tschuba.basarix.services.ItemService;
import tschuba.basarix.services.VendorService;
import tschuba.ez.booth.ui.components.ItemListItem;
import tschuba.ez.booth.ui.components.VendorCard;
import tschuba.ez.booth.ui.components.VendorReportCard;
import tschuba.ez.booth.ui.components.event.EventRequired;
import tschuba.ez.booth.ui.components.event.EventSelection;
import tschuba.ez.booth.ui.components.model.ItemComparator;
import tschuba.ez.booth.ui.i18n.TranslationKeys;
import tschuba.ez.booth.ui.layouts.OneColumnLayout;
import tschuba.ez.booth.ui.layouts.app.AppLayoutWithMenu;
import tschuba.commons.vaadin.CssUnit;
import tschuba.commons.vaadin.NavigateTo;
import tschuba.commons.vaadin.Notifications;

@Route(value = "reports/vendor", layout = AppLayoutWithMenu.class)
@EventRequired
public class VendorReportView extends OneColumnLayout {
    private static final Logger LOGGER = LoggerFactory.getLogger(VendorReportView.class);

    private static final Unit CSS_UNIT = Unit.EM;
    private static final CssUnit MIN_HEIGHT = CssUnit.cssUnit(40, CSS_UNIT);

    private final VendorService vendorService;
    private final ItemService itemService;
    private final VendorReportingService reportingService;
    private final VirtualList<VendorReportData<?>> vendorList;
    private final VirtualList<Item> itemList;
    private final Button printAllButton;
    private final TextField filterField;
    private VendorCard selectedItem;
    private String filterText;

    public VendorReportView(final ItemService itemService, VendorService vendorService, VendorReportingService reportingService) {
        this.vendorService = vendorService;
        this.itemService = itemService;
        this.reportingService = reportingService;

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

        filterField.setPlaceholder(getTranslation(TranslationKeys.VendorReportView.FILTER_FIELD__PLACEHOLDER));

        Tooltip.forComponent(printAllButton).withText(getTranslation(BUTTON_PRINT_ALL_RECEIPTS__TOOLTIP));

        vendorList.setRenderer(new ComponentRenderer<>(vendorData -> {
            VendorReportCard vendorReportCard = new VendorReportCard(vendorData);
            vendorReportCard.addClickListener(this::onClickVendorCard);
            return vendorReportCard;
        }));

        itemList.setRenderer(new ComponentRenderer<>(item -> {
            ItemListItem listItem = new ItemListItem(item);
            listItem.addClassNames(Padding.Top.XSMALL, Padding.Bottom.XSMALL);
            return listItem;
        }));

        updateVendorListItems();
        updateItemListItems();
    }

    private void updateVendorListItems() {
        EventSelection.get().ifPresent(event -> {
            Stream<Vendor> vendors = vendorService.allVendors(event).filter(vendor -> Optional.ofNullable(filterText).map(filter -> containsIgnoreCase(Integer.toString(vendor.getKey().getId()), filter)).orElse(true));
            Stream<VendorReportData<?>> vendorData = vendors.parallel().map(vendor -> {
                try {
                    return reportingService.basicReportData(vendor.getKey());
                } catch (ReportingException ex) {
                    throw new RuntimeException(ex);
                }
            });
            vendorList.setItems(vendorData);
        });
    }

    private void updateItemListItems() {
        Stream<Item> items;
        try {
            if (selectedItem != null) {
                Vendor vendor = selectedItem.getVendor();
                ItemComparator comparator = ItemComparator.builder().descending(ItemComparator.Field.DateTime).ascending(ItemComparator.Field.Price).build();
                VendorReportData.Basic reportData = reportingService.basicReportData(vendor.getKey());
                items = itemService.byKeys(reportData.items()).sorted(comparator);
            } else {
                items = Stream.empty();
            }
            itemList.setItems(items);
        } catch (ReportingException ex) {
            LOGGER.error("Failed to update item-list items!", ex);
            Notifications.error(getTranslation(TranslationKeys.Notifications.GENERIC_ERROR_MESSAGE), ex);
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
        EventSelection.get().ifPresent(event -> {
            VendorKey[] vendors = vendorService.allVendors(event).map(Vendor::getKey).toArray(VendorKey[]::new);
            try {
                File reportFile = reportingService.generateVendorReport(vendors);
                NavigateTo.file(reportFile).newWindow();
            } catch (ReportingException ex) {
                Notifications.error(ex.getLocalizedMessage(), ex);
            }
        });
    }
}
