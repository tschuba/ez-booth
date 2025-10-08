/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.ui.components.event;

import static tschuba.ez.booth.i18n.I18N.i18N;
import static tschuba.ez.booth.i18n.TranslationKeys.EventSelection.NOTIFICATION__NO_EVENT_SELECTED;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.RouteParameters;
import com.vaadin.flow.server.VaadinSession;
import java.util.Objects;
import java.util.Optional;
import lombok.NonNull;
import tschuba.ez.booth.model.DataModel;
import tschuba.ez.booth.ui.util.Notifications;
import tschuba.ez.booth.ui.util.Routing;
import tschuba.ez.booth.ui.views.EntryView;

public class BoothSelection {
    public static Optional<DataModel.Booth.Key> get() {
        return get(session());
    }

    public static Optional<DataModel.Booth.Key> get(@NonNull UI ui) {
        return get(ui.getSession());
    }

    public static Optional<DataModel.Booth.Key> get(@NonNull VaadinSession session) {
        return Optional.ofNullable(session.getAttribute(DataModel.Booth.Key.class));
    }

    public static void checkBeforeEnter(BeforeEnterEvent enterEvent, Component origin) {
        Class<? extends Component> originClass = origin.getClass();
        EventRequired eventRequired = originClass.getAnnotation(EventRequired.class);
        if (eventRequired != null && get().isEmpty()) {
            Notifications.warning(
                    i18N().getTranslation(
                                    NOTIFICATION__NO_EVENT_SELECTED, UI.getCurrent().getLocale()));
            RouteParameters rerouteParameters =
                    Routing.Parameters.builder().returnToView(originClass).build();
            enterEvent.rerouteTo(EntryView.class, rerouteParameters);
        }
    }

    public static void set(DataModel.Booth.Key key) {
        session().setAttribute(DataModel.Booth.Key.class, key);
    }

    public static void deleted(DataModel.Booth.Key deletedEvent) {
        get().ifPresent(
                        selectedEvent -> {
                            if (Objects.equals(selectedEvent, deletedEvent)) {
                                set(null);
                            }
                        });
    }

    private static VaadinSession session() {
        return VaadinSession.getCurrent();
    }
}
