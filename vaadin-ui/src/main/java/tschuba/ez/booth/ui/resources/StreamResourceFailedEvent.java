package tschuba.ez.booth.ui.resources;

import com.vaadin.flow.server.StreamRegistration;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class StreamResourceFailedEvent extends StreamResourceEvent implements Serializable {
    private final Exception error;

    public StreamResourceFailedEvent(StreamRegistration registration, Exception error) {
        super(registration);
        this.error = error;
    }

}
