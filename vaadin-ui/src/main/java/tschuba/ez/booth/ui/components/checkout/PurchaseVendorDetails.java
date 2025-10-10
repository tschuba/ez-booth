/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.ui.components.checkout;

import static java.util.Optional.ofNullable;
import static tschuba.ez.booth.i18n.TranslationKeys.PurchaseSummary.ITEM_COUNT__TEXT;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.theme.lumo.LumoUtility.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;
import lombok.Getter;
import tschuba.ez.booth.i18n.I18N;
import tschuba.ez.booth.i18n.TranslationKeys;
import tschuba.ez.booth.model.DataModel;
import tschuba.ez.booth.services.ServiceModel;

public class PurchaseVendorDetails extends Div {
  @Getter private final DataModel.Vendor.Key vendor;
  @Getter private Double sumOfItems = 0.0;
  private final Span vendorSumSpan;
  private final Div itemsContainer;
  private final Span vendorCountSpan;

  public PurchaseVendorDetails(DataModel.Vendor.Key vendor) {
    this(vendor, new ArrayList<>());
  }

  public PurchaseVendorDetails(
      DataModel.Vendor.Key vendor, List<ServiceModel.CheckoutItem> itemList) {
    super();
    this.vendor = vendor;

    addClassNames(Display.BLOCK, Margin.NONE, Padding.SMALL, Border.TOP, BorderColor.CONTRAST_10);

    Span vendorSpan = new Span();
    String vendorText = getTranslation(TranslationKeys.Vendor.ID__FORMAT_LONG, vendor.vendorId());
    vendorSpan.setText(vendorText);
    vendorSpan.addClassNames(FontSize.MEDIUM, FontWeight.BOLD, Margin.Left.XSMALL);

    vendorSumSpan = new Span();
    vendorSumSpan.addClassNames(Padding.NONE, Padding.Left.SMALL, Padding.Right.SMALL);
    vendorSumSpan.addClassNames(FontSize.MEDIUM, FontWeight.BOLD, TextColor.BODY);

    vendorCountSpan = new Span();
    vendorCountSpan.addClassNames(Padding.NONE, Padding.Left.SMALL, Padding.Right.SMALL);
    vendorCountSpan.addClassNames(FontSize.SMALL, TextColor.BODY);

    Div header = new Div();
    header.addClassNames(
        Display.FLEX, AlignItems.CENTER, JustifyContent.BETWEEN, Margin.Bottom.MEDIUM);
    header.add(vendorSpan, vendorCountSpan, vendorSumSpan);

    itemsContainer = new Div();
    itemsContainer.addClassNames(Display.BLOCK, Margin.NONE);
    add(header, itemsContainer);

    ofNullable(itemList).stream()
        .flatMap(Collection::stream)
        .forEach(item -> addItem(item.price()));

    updateVendorDetails();
  }

  public Stream<ServiceModel.CheckoutItem> getItems() {
    return itemsContainer
        .getChildren()
        .map(component -> (PurchaseItemBadge) component)
        .map(PurchaseItemBadge::getItem);
  }

  public void addItem(BigDecimal price) {
    ServiceModel.CheckoutItem item =
        ServiceModel.CheckoutItem.builder()
            .vendor(vendor)
            .price(price)
            .purchasedOn(LocalDateTime.now())
            .build();

    PurchaseItemBadge itemBadge = new PurchaseItemBadge(item);
    itemBadge.addItemDeletedEventListener(this::onItemDeletedEvent);
    itemBadge.addClassNames(Padding.SMALL, Margin.NONE, Margin.Right.SMALL, Margin.Bottom.SMALL);
    itemsContainer.addComponentAsFirst(itemBadge);

    updateVendorDetails();

    fireItemsChangedEvent(false, item);
  }

  public void addItemsChangedListener(ComponentEventListener<ItemsChangedEvent> listener) {
    addListener(ItemsChangedEvent.class, listener);
  }

  private void updateVendorDetails() {
    I18N.LocaleFormat format = I18N.format(getLocale());
    sumOfItems =
        getItems()
            .map(ServiceModel.CheckoutItem::price)
            .reduce(BigDecimal::add)
            .orElse(BigDecimal.ZERO)
            .doubleValue();
    vendorSumSpan.setText(format.currency().format(sumOfItems));
    long itemCount = getItems().count();
    vendorCountSpan.setText(getTranslation(ITEM_COUNT__TEXT, itemCount));
  }

  private void onItemDeletedEvent(PurchaseItemBadge.ItemDeletedEvent event) {
    if (getItems().findAny().isPresent()) {
      updateVendorDetails();
    } else {
      removeFromParent();
    }
    fireItemsChangedEvent(event.isFromClient(), event.getSource().getItem());
  }

  private void fireItemsChangedEvent(boolean fromClient, ServiceModel.CheckoutItem item) {
    fireEvent(new ItemsChangedEvent(this, fromClient, item));
  }

  @Getter
  public static class ItemsChangedEvent extends ComponentEvent<PurchaseVendorDetails> {

    private final ServiceModel.CheckoutItem item;

    public ItemsChangedEvent(
        PurchaseVendorDetails source, boolean fromClient, ServiceModel.CheckoutItem item) {
      super(source, fromClient);
      this.item = item;
    }
  }
}
