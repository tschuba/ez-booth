/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.ui.renderer;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import lombok.NonNull;
import tschuba.ez.booth.i18n.I18N;
import tschuba.ez.booth.model.DataModel;

public class ColumnRenderer {

    public static class Purchase {
        public static ComponentRenderer<Span, DataModel.Purchase> sum(
                @NonNull final I18N.LocaleFormat format) {
            return new ComponentRenderer<>(
                    Span::new,
                    (span, purchase) -> {
                        span.setText(format.currency(purchase.value()));
                    });
        }

        public static ComponentRenderer<Span, DataModel.Purchase> dateTime(
                @NonNull final I18N.LocaleFormat format) {
            return new ComponentRenderer<>(
                    Span::new,
                    (span, purchase) -> {
                        span.setText(format.dateTime(purchase.purchasedOn()));
                    });
        }
    }
}
