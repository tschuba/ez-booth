package tschuba.ez.booth.ui.components.checkout;

import com.vaadin.flow.component.ComponentEvent;
import lombok.Getter;
import tschuba.ez.booth.services.ServiceModel;

@Getter
public class CheckoutConfirmedEvent extends ComponentEvent<CheckoutConfirmationDialog> {
    private final ServiceModel.Checkout checkout;

    public CheckoutConfirmedEvent(CheckoutConfirmationDialog source, boolean fromClient, ServiceModel.Checkout checkout) {
        super(source, fromClient);
        this.checkout = checkout;
    }
}
