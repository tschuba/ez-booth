package tschuba.ez.booth.ui.components.checkout;

import com.vaadin.flow.component.ComponentEvent;
import lombok.Getter;
import tschuba.basarix.services.dto.Checkout;

@Getter
public class CheckoutConfirmedEvent extends ComponentEvent<CheckoutConfirmationDialog> {
    private final Checkout checkout;

    public CheckoutConfirmedEvent(CheckoutConfirmationDialog source, boolean fromClient, Checkout checkout) {
        super(source, fromClient);
        this.checkout = checkout;
    }
}
