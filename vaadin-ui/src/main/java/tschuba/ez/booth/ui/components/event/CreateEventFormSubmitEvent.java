package tschuba.ez.booth.ui.components.event;

import com.vaadin.flow.component.ComponentEvent;
import lombok.Getter;
import tschuba.basarix.data.model.Event;

@Getter
public class CreateEventFormSubmitEvent extends ComponentEvent<UpsertEventForm> {
    private final Event event;

    public CreateEventFormSubmitEvent(UpsertEventForm source, boolean fromClient, Event event) {
        super(source, fromClient);
        this.event = event;
    }

}
