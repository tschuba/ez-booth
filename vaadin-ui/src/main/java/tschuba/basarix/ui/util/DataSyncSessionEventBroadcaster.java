/* Licensed under MIT

Copyright (c) 2025 Thomas Schulte-Bahrenberg */
package tschuba.basarix.ui.util;

import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.spring.annotation.SpringComponent;
import java.util.LinkedList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import tschuba.basarix.sync.DataSyncSessionEvent;

@SpringComponent
public class DataSyncSessionEventBroadcaster {

    static final DataSyncSessionEventBroadcaster INSTANCE = createInstance();

    final Executor executor;
    final LinkedList<Consumer<DataSyncSessionEvent>> listeners;

    DataSyncSessionEventBroadcaster(Executor executor, LinkedList<Consumer<DataSyncSessionEvent>> listeners) {
        this.executor = executor;
        this.listeners = listeners;
    }

    private static DataSyncSessionEventBroadcaster createInstance() {
        return new DataSyncSessionEventBroadcaster(Executors.newSingleThreadExecutor(), new LinkedList<>());
    }

    @Bean
    public static DataSyncSessionEventBroadcaster getInstance() {
        return INSTANCE;
    }

    Registration addListener(Consumer<DataSyncSessionEvent> listener) {
        listeners.add(listener);

        return () -> {
            synchronized (DataSyncSessionEventBroadcaster.class) {
                listeners.remove(listener);
            }
        };
    }

    void notifyListeners(DataSyncSessionEvent event) {
        listeners.forEach(listener -> executor.execute(() -> listener.accept(event)));
    }

    public static synchronized Registration register(Consumer<DataSyncSessionEvent> listener) {
        return INSTANCE.addListener(listener);
    }

    @EventListener
    void onSessionEvent(DataSyncSessionEvent event) {
        synchronized (DataSyncSessionEventBroadcaster.class) {
            notifyListeners(event);
        }
    }
}
