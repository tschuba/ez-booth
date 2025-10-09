/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.ui.util;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.dom.impl.ImmutableEmptyStyle;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import lombok.NonNull;
import tschuba.ez.booth.i18n.TranslationKeys;

public class UIUtil {
    public static void traverseParents(Component from, Function<Component, Boolean> function) {
        from.getParent()
                .ifPresent(
                        parent -> {
                            if (function.apply(parent)) {
                                traverseParents(parent, function);
                            }
                        });
    }

    public static <T> void traverseAllOfType(
            Component component, Class<T> type, Consumer<T> consumer) {
        traverseAllOfType(Stream.of(component), type, consumer);
    }

    public static <T> void traverseAllOfType(
            Stream<Component> components, Class<T> type, Consumer<T> consumer) {
        components.forEach(
                component -> {
                    if (type.isAssignableFrom(component.getClass())) {
                        consumer.accept(type.cast(component));
                    }
                    traverseAllOfType(component.getChildren(), type, consumer);
                });
    }

    public static void optimizeViewForPrinting(Component component) {
        traverseAllOfType(
                component,
                HasStyle.class,
                hasStyle -> {
                    Style style = hasStyle.getStyle();
                    if (!(style instanceof ImmutableEmptyStyle)) {
                        style.setColor("black");
                        style.set("border-color", "black");
                    }
                });
    }

    /**
     * @param component The component for which the page title is to be generated.
     * @return The title of the page, which is a combination of the app title and the view title.
     */
    public static String pageTitle(@NonNull Component component) {
        String viewName = component.getClass().getSimpleName();
        String viewTranslationKey = String.format("%s.title", viewName);
        String viewTitle = component.getTranslation(viewTranslationKey);
        String appTitle = component.getTranslation(TranslationKeys.App.TITLE);
        return String.format("%s - %s", appTitle, viewTitle);
    }
}
