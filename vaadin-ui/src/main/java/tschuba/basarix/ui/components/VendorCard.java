/* Licensed under MIT

Copyright (c) 2025 Thomas Schulte-Bahrenberg */
package tschuba.basarix.ui.components;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.avatar.AvatarVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import lombok.Getter;
import tschuba.basarix.data.model.Vendor;
import tschuba.basarix.data.model.VendorKey;
import tschuba.basarix.ui.renderer.VendorRenderer;
import tschuba.basarix.ui.renderer.VendorRenderer.Format;

import static com.vaadin.flow.theme.lumo.LumoUtility.*;

@Getter
public class VendorCard extends Div implements Selectable {
    private final Vendor vendor;
    private final Span nameSpan;
    private final Avatar avatar;
    private boolean selected;

    public VendorCard(Vendor vendor) {
        this.vendor = vendor;
        addClassNames(Display.FLEX, AlignItems.CENTER, JustifyContent.BETWEEN, Padding.SMALL, BorderRadius.MEDIUM);

        avatar = new Avatar();
        avatar.addThemeVariants(AvatarVariant.LUMO_LARGE);
        avatar.addClassNames(Margin.Right.MEDIUM);
        Integer vendorId = vendor.getKey().getId();
        avatar.setColorIndex(Integer.remainderUnsigned(vendorId, 7));

        nameSpan = new Span();

        Div contentContainer = new Div(avatar, nameSpan);
        contentContainer.addClassNames(Display.FLEX, AlignItems.CENTER, JustifyContent.START, BorderRadius.MEDIUM);

        add(contentContainer);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);

        VendorRenderer vendorRenderer = VendorRenderer.of(attachEvent.getUI());
        VendorKey key = vendor.getKey();

        nameSpan.setText(vendorRenderer.keyToString(Format.Long, key));

        String abbreviation = vendorRenderer.keyToString(Format.Short, key);
        avatar.setAbbreviation(abbreviation);
    }

    public void select() {
        selected = true;
        Selectable.super.select();
    }

    @Override
    public void unselect() {
        selected = false;
        Selectable.super.unselect();
    }

}
