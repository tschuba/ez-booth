package tschuba.basarix.ui.components.event;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.RouteParameters;
import com.vaadin.flow.server.VaadinSession;
import lombok.NonNull;
import tschuba.basarix.data.model.EventKey;
import tschuba.basarix.ui.util.RoutingParameters;
import tschuba.basarix.ui.views.EntryView;
import tschuba.commons.vaadin.Notifications;

import java.util.Objects;
import java.util.Optional;

import static tschuba.basarix.ui.i18n.TranslationKeys.EventSelection.NOTIFICATION__NO_EVENT_SELECTED;
import static tschuba.commons.vaadin.i18n.I18N.i18N;

public class EventSelection {
    public static Optional<EventKey> get() {
        return get(session());
    }

    public static Optional<EventKey> get(@NonNull UI ui) {
        return get(ui.getSession());
    }

    public static Optional<EventKey> get(@NonNull VaadinSession session) {
        return Optional.ofNullable(session.getAttribute(EventKey.class));
    }

    public static void checkBeforeEnter(BeforeEnterEvent enterEvent, Component origin) {
        Class<? extends Component> originClass = origin.getClass();
        EventRequired eventRequired = originClass.getAnnotation(EventRequired.class);
        if (eventRequired != null && get().isEmpty()) {
            Notifications.warning(i18N().getTranslation(NOTIFICATION__NO_EVENT_SELECTED, UI.getCurrent().getLocale()));
            RouteParameters rerouteParameters = RoutingParameters.builder().returnToView(originClass).build();
            enterEvent.rerouteTo(EntryView.class, rerouteParameters);
        }
    }

    public static void set(EventKey key) {
        session().setAttribute(EventKey.class, key);
    }

    public static void deleted(EventKey deletedEvent) {
        get().ifPresent(selectedEvent -> {
            if (Objects.equals(selectedEvent, deletedEvent)) {
                set(null);
            }
        });
    }

    private static VaadinSession session() {
        return VaadinSession.getCurrent();
    }
}
