package tschuba.ez.booth.ui.renderer;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import lombok.NonNull;
import tschuba.commons.vaadin.i18n.Formats;

import java.util.Locale;

public class ColumnRenderer {

    public static class Purchase {
        public static ComponentRenderer<Span, tschuba.basarix.data.model.Purchase> sum(@NonNull final Formats formats, @NonNull final Locale locale) {
            return new ComponentRenderer<>(Span::new, (span, purchase) -> {
                span.setText(formats.currency(purchase.getValue(), locale));
            });
        }

        public static ComponentRenderer<Span, tschuba.basarix.data.model.Purchase> dateTime(@NonNull final Formats formats, @NonNull final Locale locale) {
            return new ComponentRenderer<>(Span::new, (span, purchase) -> {
                span.setText(formats.dateTime(purchase.getDateTime(), locale));
            });
        }
    }
}
