/* Licensed under MIT

Copyright (c) 2025 Thomas Schulte-Bahrenberg */
package tschuba.ez.booth.ui.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.shared.Registration;
import lombok.Getter;

public class ToggleButton extends Button {
    private static final String STATE_PROPERTY = "x-toggled";

    public ToggleButton(Component icon) {
        super(icon);
    }

    public boolean isToggled() {
        return getElement().getProperty(STATE_PROPERTY, false);
    }

    public void setToggled(boolean toggled) {
        boolean wasToggled = isToggled();
        getElement().setProperty(STATE_PROPERTY, toggled);
        if (wasToggled != toggled) {
            fireEvent(new ToggleEvent(this, toggled, false));
        }
    }

    public void toggle() {
        this.setToggled(!this.isToggled());
    }

    public Registration addToggleListener(ComponentEventListener<ToggleEvent> listener) {
        return addListener(ToggleEvent.class, listener);
    }

    @Getter
    public static class ToggleEvent extends ComponentEvent<ToggleButton> {
        private final boolean toggled;

        public ToggleEvent(ToggleButton source, boolean toggled, boolean fromClient) {
            super(source, fromClient);
            this.toggled = toggled;
        }
    }
}
