/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.ui.views;

import static com.vaadin.flow.theme.lumo.LumoUtility.*;
import static tschuba.ez.booth.i18n.Formats.formats;
import static tschuba.ez.booth.i18n.TranslationKeys.PurchaseReceiptPrintView.*;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import java.util.Optional;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import tschuba.ez.booth.data.PurchaseRepository;
import tschuba.ez.booth.model.DataModel;
import tschuba.ez.booth.model.EntitiesMapper;
import tschuba.ez.booth.model.EntityModel;
import tschuba.ez.booth.ui.components.checkout.ReportPrintViewItemCard;
import tschuba.ez.booth.ui.components.model.ItemComparator;
import tschuba.ez.booth.ui.layouts.OneColumnLayout;
import tschuba.ez.booth.ui.util.Notifications;
import tschuba.ez.booth.ui.util.Routing;
import tschuba.ez.booth.ui.util.UIUtil;

@Route(value = "reports/purchase/receipt/:eventId/:purchaseId")
public class PurchaseReceiptPrintView extends OneColumnLayout implements BeforeEnterObserver {
    private final Span purchaseIdValue;
    private final Div itemsContainer;
    private final PurchaseRepository purchases;
    private final Span itemCountValue;
    private final Span dateTimeValue;
    private final Span purchaseSumValue;

    public PurchaseReceiptPrintView(@Autowired @NonNull PurchaseRepository purchases) {
        this.purchases = purchases;

        setTitle(getTranslation(TITLE));

        Main content = new Main();
        content.setWidth(210, Unit.MM);
        content.addClassNames(
                Display.FLEX,
                FlexDirection.COLUMN,
                Flex.GROW_NONE,
                Height.FULL,
                JustifyContent.START,
                Padding.NONE,
                Padding.Left.SMALL,
                Padding.Right.SMALL);
        setContent(content);

        purchaseIdValue = createField();
        Component purchaseIdField =
                wrapFieldWithLabel(getTranslation(PURCHASE_ID__LABEL), purchaseIdValue);

        dateTimeValue = createField();
        Component dateTimeField =
                wrapFieldWithLabel(getTranslation(DATE_TIME__LABEL), dateTimeValue);

        itemCountValue = createField();
        Component itemCountField =
                wrapFieldWithLabel(getTranslation(ITEM_COUNT__LABEL), itemCountValue);

        purchaseSumValue = createField();
        purchaseSumValue.addClassNames(FontWeight.EXTRABOLD);
        Component purchaseSumField =
                wrapFieldWithLabel(getTranslation(SUM__LABEL), purchaseSumValue);

        itemsContainer = new Div();
        itemsContainer.addClassNames(Display.GRID, Grid.Column.COLUMNS_5, Gap.LARGE, Padding.SMALL);

        Div headerLine = new Div();
        headerLine.addClassNames(
                Margin.Bottom.LARGE,
                Padding.SMALL,
                Padding.Top.NONE,
                Border.BOTTOM,
                BorderColor.CONTRAST_50);
        headerLine.add(purchaseIdField);

        Div footerLine = new Div();
        footerLine.addClassNames(
                Display.FLEX,
                JustifyContent.BETWEEN,
                Margin.Top.LARGE,
                Padding.SMALL,
                Padding.Top.MEDIUM,
                Border.TOP,
                BorderColor.CONTRAST_50);
        footerLine.add(dateTimeField, itemCountField, purchaseSumField);

        content.add(headerLine, itemsContainer, footerLine);

        UIUtil.optimizeViewForPrinting(this);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<DataModel.Purchase.Key> purchaseKey =
                Routing.Parameters.parser(event.getRouteParameters()).purchaseKey();
        if (purchaseKey.isEmpty()) {
            String message = getTranslation(NOTIFICATION__ILLEGAL_ARGUMENTS);
            Notifications.error(message);
            return;
        }

        Optional<EntityModel.Purchase> purchaseByKey =
                purchases.findById(EntitiesMapper.objectToEntity(purchaseKey.get()));
        if (purchaseByKey.isEmpty()) {
            Notifications.warning(
                    getTranslation(NOTIFICATION__PURCHASE_NOT_FOUND, purchaseKey.get()));
            return;
        }

        DataModel.Purchase purchase = purchaseByKey.map(EntitiesMapper::entityToObject).get();
        purchaseIdValue.setText(purchase.key().purchaseId());
        dateTimeValue.setText(formats().dateTime(purchase.purchasedOn(), getLocale()));
        itemCountValue.setText(Integer.toString(purchase.items().size()));
        purchaseSumValue.setText(formats().currency(purchase.value()));

        itemsContainer.removeAll();
        ItemComparator comparator =
                ItemComparator.builder()
                        .ascending(ItemComparator.Field.DateTime)
                        .ascending(ItemComparator.Field.Price)
                        .build();
        purchase.items().stream()
                .sorted(comparator)
                .map(
                        item -> {
                            ReportPrintViewItemCard itemCard = new ReportPrintViewItemCard(item);
                            itemCard.setShowVendor(true);
                            return itemCard;
                        })
                .forEach(itemsContainer::add);
    }

    private Span createField() {
        Span field = new Span();
        field.addClassNames(Padding.Left.SMALL);
        return field;
    }

    private Span createField(String text) {
        Span field = createField();
        field.setText(text);
        return field;
    }

    /**
     * @param labelText text of the field label
     * @return the value field
     */
    private Component wrapFieldWithLabel(String labelText, Component valueField) {
        Span label = createField(labelText);
        return new Div(label, valueField);
    }
}
