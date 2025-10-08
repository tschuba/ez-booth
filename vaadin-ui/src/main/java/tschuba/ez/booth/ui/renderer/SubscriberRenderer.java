/* Licensed under MIT

Copyright (c) 2025 Thomas Schulte-Bahrenberg */
package tschuba.ez.booth.ui.renderer;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import jakarta.annotation.Nonnull;
import tschuba.basarix.sync.Subscriber;
import tschuba.basarix.sync.SubscriberId;
import tschuba.ez.booth.ui.util.BadgeBuilder;
import tschuba.commons.vaadin.components.Spacing;

public class SubscriberRenderer {
    public static ComponentRenderer<Component, Subscriber> renderSubscriber() {
        return new ComponentRenderer<>(subscriber -> renderSubscriber(subscriber));
    }

    public static ComponentRenderer<Component, SubscriberId> renderId() {
        return new ComponentRenderer<>(subscriberId -> renderId(subscriberId));
    }

    public static HorizontalLayout renderSubscriber(@Nonnull Subscriber subscriber) {
        HorizontalLayout layout = renderId(subscriber.id());
        Span hostSpan = BadgeBuilder.badge().small().apply(new Span(subscriber.apiBasePath().getHost()));
        layout.add(hostSpan);
        return layout;
    }

    public static HorizontalLayout renderId(@Nonnull SubscriberId id) {
        Span nameSpan = BadgeBuilder.primary().apply(new Span(id.name()));
        Span idSpan = BadgeBuilder.badge().contrast().apply(new Span(id.id()));
        HorizontalLayout layout = new HorizontalLayout(nameSpan, idSpan);
        Spacing.spacing(layout).xsmall();
        return layout;
    }
}
