package tschuba.ez.booth.ui.resources;

import com.vaadin.flow.server.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;

public class StreamResourceHandler {
    private final StreamRegistration registration;
    private final Collection<StreamResourceListener> listeners = new ArrayList<>();

    private StreamResourceHandler(StreamRegistration registration) {
        this.registration = registration;
    }

    public static StreamResourceHandler createAndRegister(String name, InputStreamFactory factory) {
        ObservableStreamResourceWriter writer = ObservableStreamResourceWriter.create(factory);
        StreamResource resource = new StreamResource(name, writer);

        StreamResourceRegistry registry = VaadinSession.getCurrent().getResourceRegistry();
        StreamRegistration registration = registry.registerResource(resource);

        StreamResourceHandler handler = new StreamResourceHandler(registration);
        writer.addListener(new StreamResourceWriterListener() {
            @Override
            public void accepted() {
                handler.onWriterAccepted();
            }

            @Override
            public void failed(StreamResourceWriterFailedEvent event) {
                handler.onWriterFailed(event);
            }
        });
        return handler;
    }

    public StreamResource resource() {
        return (StreamResource) registration.getResource();
    }

    public URI resourceUri() {
        return registration.getResourceUri();
    }

    public void addListener(StreamResourceListener listener) {
        this.listeners.add(listener);
    }

    private void onWriterAccepted() {
        StreamResourceEvent resourceEvent = new StreamResourceEvent(registration);
        listeners.forEach(listener -> listener.consumed(resourceEvent));
    }

    private void onWriterFailed(StreamResourceWriterFailedEvent event) {
        StreamResourceFailedEvent resourceEvent = new StreamResourceFailedEvent(registration, event.error());
        listeners.forEach(listener -> listener.failed(resourceEvent));
    }
}
