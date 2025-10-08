/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.ui.resources;

import com.vaadin.flow.server.InputStreamFactory;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.StreamResourceWriter;
import com.vaadin.flow.server.VaadinSession;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ObservableStreamResourceWriter implements StreamResourceWriter {
    private final StreamResourceWriter delegate;
    private final List<StreamResourceWriterListener> listeners = new ArrayList<>();

    public ObservableStreamResourceWriter(StreamResourceWriter writer) {
        Objects.requireNonNull(writer, "writer must not be null!");
        this.delegate = writer;
    }

    public static ObservableStreamResourceWriter create(InputStreamFactory factory) {
        Objects.requireNonNull(factory, "factory must not be null!");
        StreamResource resource = new StreamResource(UUID.randomUUID().toString(), factory);
        StreamResourceWriter writer = resource.getWriter();
        return new ObservableStreamResourceWriter(writer);
    }

    public void addListener(StreamResourceWriterListener listener) {
        listeners.add(listener);
    }

    @Override
    public void accept(OutputStream stream, VaadinSession session) throws IOException {
        try {
            delegate.accept(stream, session);

            listeners.forEach(StreamResourceWriterListener::accepted);
        } catch (Exception ex) {
            StreamResourceWriterFailedEvent event = new StreamResourceWriterFailedEvent(ex);
            listeners.forEach(listener -> listener.failed(event));
            throw new RuntimeException(ex);
        }
    }
}
