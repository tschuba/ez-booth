/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.ui.util;

import static tschuba.ez.booth.i18n.TranslationKeys.Notifications.COPIED_TO_CLIPBOARD__MESSAGE;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.shared.Registration;
import java.io.Serializable;
import java.util.function.Consumer;
import java.util.function.Supplier;
import lombok.NonNull;

/**
 * Utility class for working with buttons.
 */
public class Buttons {
    public static <C extends Button> ComponentEventListener<ClickEvent<C>> enableAfterClick(
            @NonNull Consumer<ClickEvent<C>> handler) {
        return clickEvent -> {
            try {
                handler.accept(clickEvent);
            } finally {
                clickEvent.getSource().setEnabled(true);
            }
        };
    }

    public static Registration disableUntilAfterClick(
            @NonNull Button button, @NonNull Consumer<ClickEvent<Button>> handler) {
        button.setDisableOnClick(true);
        return button.addClickListener(enableAfterClick(handler));
    }

    /**
     * Creates a click listener that copies the value provided by the given supplier to the browser's clipboard.
     * @param valueSupplier the supplier providing the value to be copied
     * @return the click listener
     * @param <C> the type of button
     */
    public static <C extends Button> ComponentEventListener<ClickEvent<C>> copyToClipboard(
            @NonNull Supplier<Serializable> valueSupplier) {
        return clickEvent ->
                clickEvent
                        .getSource()
                        .getUI()
                        .ifPresent(
                                ui -> {
                                    ui.getPage()
                                            .executeJs(
                                                    """
                                                    window.copyToClipboard = (str) => {
                                                      const textarea = document.createElement("textarea");
                                                      textarea.value = str;
                                                      textarea.style.position = "absolute";
                                                      textarea.style.opacity = "0";
                                                      document.body.appendChild(textarea);
                                                      textarea.select();
                                                      document.execCommand("copy");
                                                      document.body.removeChild(textarea);
                                                    };
                                                    window.copyToClipboard($0)\
                                                    """,
                                                    valueSupplier.get());
                                    Notifications.message(
                                            ui.getTranslation(COPIED_TO_CLIPBOARD__MESSAGE));
                                });
    }
}
