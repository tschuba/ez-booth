/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.ui.components;

import static com.vaadin.flow.theme.lumo.LumoUtility.*;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.avatar.AvatarVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import lombok.Getter;
import tschuba.ez.booth.model.DataModel;
import tschuba.ez.booth.ui.renderer.VendorRenderer;
import tschuba.ez.booth.ui.renderer.VendorRenderer.Format;

@Getter
public class VendorCard extends Div implements Selectable {
    private final DataModel.Vendor vendor;
    private final Span nameSpan;
    private final Avatar avatar;
    private boolean selected;

    public VendorCard(DataModel.Vendor vendor) {
        this.vendor = vendor;
        addClassNames(
                Display.FLEX,
                AlignItems.CENTER,
                JustifyContent.BETWEEN,
                Padding.SMALL,
                BorderRadius.MEDIUM);

        avatar = new Avatar();
        avatar.addThemeVariants(AvatarVariant.LUMO_LARGE);
        avatar.addClassNames(Margin.Right.MEDIUM);
        String vendorId = vendor.key().vendorId();
        // avatar.setColorIndex(Integer.remainderUnsigned(vendorId, 7));

        nameSpan = new Span();

        Div contentContainer = new Div(avatar, nameSpan);
        contentContainer.addClassNames(
                Display.FLEX, AlignItems.CENTER, JustifyContent.START, BorderRadius.MEDIUM);

        add(contentContainer);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);

        VendorRenderer vendorRenderer = VendorRenderer.of(attachEvent.getUI());
        DataModel.Vendor.Key key = vendor.key();

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
