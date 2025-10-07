/* Licensed under MIT

Copyright (c) 2025 Thomas Schulte-Bahrenberg */
package tschuba.basarix.ui.util;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.dom.ThemeList;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Stream;
import lombok.NonNull;
import tschuba.commons.vaadin.components.Badges;

public class BadgeBuilder implements Consumer<Span> {
    static final String PRIMARY = "primary";
    static final String PILL = "pill";
    static final String SMALL = "small";

    private final boolean primary;
    private boolean contrast;
    private boolean pill;
    private String variant;
    private boolean small;

    private BadgeBuilder(boolean primary) {
        this.primary = primary;
    }

    public BadgeBuilder success() {
        return variant(Badges.SUCCESS);
    }

    public BadgeBuilder warning() {
        return variant(Badges.WARNING);
    }

    public BadgeBuilder error() {
        return variant(Badges.ERROR);
    }

    public BadgeBuilder contrast(boolean contrast) {
        this.contrast = contrast;
        return this;
    }

    public BadgeBuilder contrast() {
        return this.contrast(true);
    }

    public BadgeBuilder pill(boolean pill) {
        this.pill = pill;
        return this;
    }

    public BadgeBuilder pill() {
        return this.pill(true);
    }

    public BadgeBuilder small() {
        return small(true);
    }

    public BadgeBuilder small(boolean small) {
        this.small = small;
        return this;
    }

    private BadgeBuilder variant(String variant) {
        this.variant = variant;
        return this;
    }

    public static BadgeBuilder badge() {
        return new BadgeBuilder(false);
    }

    public static BadgeBuilder primary() {
        return new BadgeBuilder(true);
    }

    @Override
    public void accept(@NonNull Span span) {
        String emphasis = primary ? PRIMARY : null;
        String contrasted = contrast ? Badges.CONTRAST : null;
        String shape = pill ? PILL : null;
        String size = small ? SMALL : null;
        ThemeList themeList = span.getElement().getThemeList();
        Stream.of(Badges.BADGE, variant, contrasted, emphasis, shape, size).filter(Objects::nonNull).forEach(themeList::add);
    }

    public Span apply(@NonNull Span span) {
        accept(span);
        return span;
    }

    public Span build() {
        return apply(new Span());
    }
}
