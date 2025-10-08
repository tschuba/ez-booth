package tschuba.ez.booth.ui.components.checkout;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.theme.lumo.LumoUtility.*;
import lombok.Getter;
import tschuba.basarix.data.model.Item;
import tschuba.basarix.data.model.ItemKey;
import tschuba.basarix.data.model.VendorKey;
import tschuba.ez.booth.ui.i18n.TranslationKeys;

import java.text.Format;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Optional.ofNullable;
import static tschuba.ez.booth.ui.i18n.TranslationKeys.PurchaseSummary.ITEM_COUNT__TEXT;
import static tschuba.commons.vaadin.i18n.Formats.formats;

public class PurchaseVendorDetails extends Div {
    @Getter
    private final VendorKey vendor;
    @Getter
    private Double sumOfItems = 0.0;
    private final Span vendorSumSpan;
    private final Div itemsContainer;
    private final Span vendorCountSpan;

    public PurchaseVendorDetails(VendorKey vendor) {
        this(vendor, new ArrayList<>());
    }

    public PurchaseVendorDetails(VendorKey vendor, List<Item> itemList) {
        super();
        this.vendor = vendor;

        addClassNames(Display.BLOCK, Margin.NONE, Padding.SMALL, Border.TOP, BorderColor.CONTRAST_10);

        Span vendorSpan = new Span();
        String vendorText = getTranslation(TranslationKeys.Vendor.ID__FORMAT_LONG, vendor.getId());
        vendorSpan.setText(vendorText);
        vendorSpan.addClassNames(FontSize.MEDIUM, FontWeight.BOLD, Margin.Left.XSMALL);

        vendorSumSpan = new Span();
        vendorSumSpan.addClassNames(Padding.NONE, Padding.Left.SMALL, Padding.Right.SMALL);
        vendorSumSpan.addClassNames(FontSize.MEDIUM, FontWeight.BOLD, TextColor.BODY);

        vendorCountSpan = new Span();
        vendorCountSpan.addClassNames(Padding.NONE, Padding.Left.SMALL, Padding.Right.SMALL);
        vendorCountSpan.addClassNames(FontSize.SMALL, TextColor.BODY);

        Div header = new Div();
        header.addClassNames(Display.FLEX, AlignItems.CENTER, JustifyContent.BETWEEN, Margin.Bottom.MEDIUM);
        header.add(vendorSpan, vendorCountSpan, vendorSumSpan);

        itemsContainer = new Div();
        itemsContainer.addClassNames(Display.BLOCK, Margin.NONE);
        add(header, itemsContainer);

        ofNullable(itemList).stream().flatMap(Collection::stream).forEach(item -> addItem(item.getPrice()));

        updateVendorDetails();
    }

    public Stream<Item> getItems() {
        return itemsContainer.getChildren().map(component -> (PurchaseItemBadge) component).map(PurchaseItemBadge::getItem);
    }

    public void addItem(Double price) {
        Item item = Item.builder()
                .setKey(ItemKey.of(vendor.getEvent()))
                .setVendor(vendor)
                .setPrice(price)
                .setDateTime(LocalDateTime.now())
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
        Format format = formats().currency(getLocale());
        sumOfItems = getItems().mapToDouble(Item::getPrice).sum();
        vendorSumSpan.setText(format.format(sumOfItems));
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

    private void fireItemsChangedEvent(boolean fromClient, Item item) {
        fireEvent(new ItemsChangedEvent(this, fromClient, item));
    }

    @Getter
    public static class ItemsChangedEvent extends ComponentEvent<PurchaseVendorDetails> {

        private final Item item;

        public ItemsChangedEvent(PurchaseVendorDetails source, boolean fromClient, Item item) {
            super(source, fromClient);
            this.item = item;
        }

    }
}
