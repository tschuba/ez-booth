package tschuba.ez.booth.ui.components.checkout;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.theme.lumo.LumoUtility.Background;
import com.vaadin.flow.theme.lumo.LumoUtility.BorderRadius;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import com.vaadin.flow.theme.lumo.LumoUtility.TextColor;
import lombok.Getter;
import org.vaadin.lineawesome.LineAwesomeIcon;
import tschuba.basarix.data.model.Item;
import tschuba.basarix.data.model.VendorKey;

import java.text.Format;

import static tschuba.ez.booth.ui.i18n.TranslationKeys.Vendor.ID__FORMAT_SHORT;
import static tschuba.commons.vaadin.i18n.Formats.formats;

@Getter
public class PurchaseItemBadge extends Button {
    private final Item item;

    public PurchaseItemBadge(Item item) {
        this.item = item;

        VendorKey vendor = item.getVendor();

        addClassNames(BorderRadius.MEDIUM, Background.CONTRAST_5, TextColor.BODY);
        setIcon(LineAwesomeIcon.TRASH_SOLID.create());
        setIconAfterText(true);
        addClickListener(this::onDeleteButtonClick);

        Format decimalFormat = formats().currency(getLocale());
        setText(decimalFormat.format(item.getPrice()));

        Span vendorSpan = new Span();
        String vendorId = getTranslation(ID__FORMAT_SHORT, vendor.getId());
        vendorSpan.setText(vendorId);
        vendorSpan.addClassNames(Margin.End.XSMALL);

        Span priceSpan = new Span(decimalFormat.format(item.getPrice()));
        priceSpan.addClassNames(Margin.End.SMALL);

        Button deleteButton = new Button();
        deleteButton.setIcon(LineAwesomeIcon.TRASH_SOLID.create());
        deleteButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE, ButtonVariant.LUMO_SMALL);
        deleteButton.addClickListener(this::onDeleteButtonClick);
    }

    public void addItemDeletedEventListener(ComponentEventListener<ItemDeletedEvent> listener) {
        addListener(ItemDeletedEvent.class, listener);
    }

    private void onDeleteButtonClick(ClickEvent<Button> event) {
        removeFromParent();
        fireEvent(new ItemDeletedEvent(this, false));
    }

    public static class ItemDeletedEvent extends ComponentEvent<PurchaseItemBadge> {
        private ItemDeletedEvent(PurchaseItemBadge source, boolean fromClient) {
            super(source, fromClient);
        }
    }
}
