/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.ui.renderer;

import static tschuba.ez.booth.i18n.TranslationKeys.Vendor.ID__FORMAT_LONG;
import static tschuba.ez.booth.i18n.TranslationKeys.Vendor.ID__FORMAT_SHORT;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.function.ValueProvider;
import java.util.Optional;
import tschuba.ez.booth.model.DataModel;

public class VendorRenderer extends HasUI {
    public enum Format {
        Short,
        Long
    }

    public VendorRenderer(UI ui) {
        super(ui);
    }

    /**
     * @deprecated use {@link #of(UI)} instead
     */
    @Deprecated
    public static VendorRenderer of(Optional<UI> ui) {
        return of(ui, VendorRenderer::new);
    }

    public static VendorRenderer of(UI ui) {
        return of(Optional.ofNullable(ui), VendorRenderer::new);
    }

    public ValueProvider<DataModel.Vendor.Key, String> keyToString(Format format) {
        String translationKey = (format == Format.Short) ? ID__FORMAT_SHORT : ID__FORMAT_LONG;
        return vendorKey -> getUi().getTranslation(translationKey, vendorKey.vendorId());
    }

    public String keyToString(Format format, DataModel.Vendor.Key vendor) {
        return keyToString(format).apply(vendor);
    }
}
