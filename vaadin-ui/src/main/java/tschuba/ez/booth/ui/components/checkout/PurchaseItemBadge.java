/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.ui.components.checkout;

import static tschuba.ez.booth.i18n.TranslationKeys.Vendor.ID__FORMAT_SHORT;

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
import java.text.Format;
import lombok.Getter;
import org.vaadin.lineawesome.LineAwesomeIcon;
import tschuba.ez.booth.i18n.I18N;
import tschuba.ez.booth.model.DataModel;

@Getter
public class PurchaseItemBadge extends Button {
    private final DataModel.PurchaseItem item;

    public PurchaseItemBadge(DataModel.PurchaseItem item) {
        this.item = item;

        DataModel.Vendor.Key vendor = item.vendor();

        addClassNames(BorderRadius.MEDIUM, Background.CONTRAST_5, TextColor.BODY);
        setIcon(LineAwesomeIcon.TRASH_SOLID.create());
        setIconAfterText(true);
        addClickListener(this::onDeleteButtonClick);

        I18N.LocaleFormat format = I18N.i18N().format(getLocale());
        Format decimalFormat = format.currency();
        setText(decimalFormat.format(item.price()));

        Span vendorSpan = new Span();
        String vendorId = getTranslation(ID__FORMAT_SHORT, vendor.vendorId());
        vendorSpan.setText(vendorId);
        vendorSpan.addClassNames(Margin.End.XSMALL);

        Span priceSpan = new Span(decimalFormat.format(item.price()));
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
