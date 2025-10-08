/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.ui.renderer;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import java.util.Locale;
import lombok.NonNull;
import tschuba.ez.booth.model.DataModel;
import tschuba.ez.booth.i18n.Formats;

public class ColumnRenderer {

    public static class Purchase {
        public static ComponentRenderer<Span, DataModel.Purchase> sum(
                @NonNull final Formats formats, @NonNull final Locale locale) {
            return new ComponentRenderer<>(
                    Span::new,
                    (span, purchase) -> {
                        span.setText(formats.currency(purchase.value(), locale));
                    });
        }

        public static ComponentRenderer<Span, DataModel.Purchase> dateTime(
                @NonNull final Formats formats, @NonNull final Locale locale) {
            return new ComponentRenderer<>(
                    Span::new,
                    (span, purchase) -> {
                        span.setText(formats.dateTime(purchase.purchasedOn(), locale));
                    });
        }
    }
}
