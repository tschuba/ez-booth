/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.ui.resources;

import com.vaadin.flow.server.StreamRegistration;
import java.io.Serializable;
import lombok.Getter;

@Getter
public class StreamResourceFailedEvent extends StreamResourceEvent implements Serializable {
    private final Exception error;

    public StreamResourceFailedEvent(StreamRegistration registration, Exception error) {
        super(registration);
        this.error = error;
    }
}
