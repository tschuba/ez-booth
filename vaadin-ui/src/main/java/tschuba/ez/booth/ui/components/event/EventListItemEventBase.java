package tschuba.ez.booth.ui.components.event;

import com.vaadin.flow.component.ComponentEvent;
import lombok.Getter;
import tschuba.basarix.data.model.Event;

@Getter
public class EventListItemEventBase extends ComponentEvent<EventListItem> {
    private final Event event;

    public EventListItemEventBase(EventListItem source, boolean fromClient, Event event) {
        super(source, fromClient);
        this.event = event;
    }

}
