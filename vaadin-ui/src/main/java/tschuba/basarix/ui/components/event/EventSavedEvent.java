package tschuba.basarix.ui.components.event;

import com.vaadin.flow.component.ComponentEvent;
import lombok.Getter;
import tschuba.basarix.data.model.Event;

@Getter
public class EventSavedEvent extends ComponentEvent<UpsertEventDialog> {
    private final Event event;

    public EventSavedEvent(UpsertEventDialog source, boolean fromClient, Event event) {
        super(source, fromClient);
        this.event = event;
    }

}
