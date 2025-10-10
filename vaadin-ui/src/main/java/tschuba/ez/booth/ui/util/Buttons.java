/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.ui.util;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.shared.Registration;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public class Buttons {
    public static <C extends Button> ComponentEventListener<ClickEvent<C>> enableAfterClick(
            Consumer<ClickEvent<C>> handler) {
        return clickEvent -> {
            try {
                handler.accept(clickEvent);
            } finally {
                clickEvent.getSource().setEnabled(true);
            }
        };
    }

    public static Registration disableUntilAfterClick(
            Button button, Consumer<ClickEvent<Button>> handler) {
        button.setDisableOnClick(true);
        return button.addClickListener(enableAfterClick(handler));
    }
}
