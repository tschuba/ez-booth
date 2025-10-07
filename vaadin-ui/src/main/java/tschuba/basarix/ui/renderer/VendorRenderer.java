/* Licensed under MIT

Copyright (c) 2025 Thomas Schulte-Bahrenberg */
package tschuba.basarix.ui.renderer;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.function.ValueProvider;
import tschuba.basarix.data.model.VendorKey;

import java.util.Optional;

import static tschuba.basarix.ui.i18n.TranslationKeys.Vendor.ID__FORMAT_LONG;
import static tschuba.basarix.ui.i18n.TranslationKeys.Vendor.ID__FORMAT_SHORT;

public class VendorRenderer extends HasUI {
    public enum Format {
        Short, Long
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

    public ValueProvider<VendorKey, String> keyToString(Format format) {
        String translationKey = (format == Format.Short) ? ID__FORMAT_SHORT : ID__FORMAT_LONG;
        return vendorKey -> getUi().getTranslation(translationKey, vendorKey.getId());
    }

    public String keyToString(Format format, VendorKey vendor) {
        return keyToString(format).apply(vendor);
    }
}
