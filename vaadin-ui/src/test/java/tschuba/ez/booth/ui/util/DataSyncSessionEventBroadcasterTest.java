/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.ui.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.vaadin.flow.shared.Registration;
import java.util.LinkedList;
import java.util.function.Consumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tschuba.basarix.sync.DataSyncSessionEvent;

/**
 * Test class for {@link DataSyncSessionEventBroadcaster}.
 */
class DataSyncSessionEventBroadcasterTest {

    private LinkedList<Consumer<DataSyncSessionEvent>> listeners;
    private Consumer<DataSyncSessionEvent> listenerMock;
    private DataSyncSessionEventBroadcaster broadcaster;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        listeners = new LinkedList<>();
        broadcaster = new DataSyncSessionEventBroadcaster(Runnable::run, listeners);

        listenerMock = mock(Consumer.class);
    }

    @Test
    void testGetInstance() {
        assertThatNoException()
                .isThrownBy(
                        () ->
                                assertThat(DataSyncSessionEventBroadcaster.getInstance())
                                        .isNotNull()
                                        .isEqualTo(DataSyncSessionEventBroadcaster.INSTANCE));
    }

    @Test
    void testAddListener() {
        Registration registration = broadcaster.addListener(listenerMock);
        assertThat(registration).isNotNull();

        assertThat(broadcaster.listeners).containsExactly(listenerMock);
    }

    @Test
    void testNotifyListeners() {
        broadcaster.addListener(listenerMock);

        DataSyncSessionEvent eventMock = mock(DataSyncSessionEvent.class);
        broadcaster.notifyListeners(eventMock);

        verify(listenerMock).accept(eventMock);
    }

    @Test
    void testOnSessionEvent() {
        broadcaster.addListener(listenerMock);

        DataSyncSessionEvent eventMock = mock(DataSyncSessionEvent.class);
        broadcaster.onSessionEvent(eventMock);

        verify(listenerMock).accept(eventMock);
    }
}
