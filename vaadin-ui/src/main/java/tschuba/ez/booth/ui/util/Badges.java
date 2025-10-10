/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.ui.util;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.dom.ThemeList;
import com.vaadin.flow.theme.lumo.LumoUtility;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Stream;
import lombok.NonNull;

/**
 * Utility class for creating and applying badge styles to Vaadin components.
 */
public class Badges implements Consumer<Span> {
  public static final String BADGE = "badge";
  public static final String SUCCESS = "success";
  public static final String WARNING = "warning";
  public static final String ERROR = "error";
  public static final String CONTRAST = "contrast";

  static final String PRIMARY = "primary";
  static final String PILL = "pill";
  static final String SMALL = "small";

  private static final Badges PRIMARY_CONTRAST = Badges.primary().contrast();

  private final boolean primary;
  private boolean contrast;
  private boolean pill;
  private String variant;
  private boolean small;

  private Badges(boolean primary) {
    this.primary = primary;
  }

  public Badges success() {
    return variant(SUCCESS);
  }

  public Badges warning() {
    return variant(WARNING);
  }

  public Badges error() {
    return variant(ERROR);
  }

  public Badges contrast(boolean contrast) {
    this.contrast = contrast;
    return this;
  }

  public Badges contrast() {
    return this.contrast(true);
  }

  public Badges pill(boolean pill) {
    this.pill = pill;
    return this;
  }

  public Badges pill() {
    return this.pill(true);
  }

  public Badges small() {
    return small(true);
  }

  public Badges small(boolean small) {
    this.small = small;
    return this;
  }

  private Badges variant(String variant) {
    this.variant = variant;
    return this;
  }

  public static Badges badge() {
    return new Badges(false);
  }

  public static Badges primary() {
    return new Badges(true);
  }

  @Override
  public void accept(@NonNull Span span) {
    String emphasis = primary ? PRIMARY : null;
    String contrasted = contrast ? CONTRAST : null;
    String shape = pill ? PILL : null;
    String size = small ? SMALL : null;
    ThemeList themeList = span.getElement().getThemeList();
    Stream.of(BADGE, variant, contrasted, emphasis, shape, size)
        .filter(Objects::nonNull)
        .forEach(themeList::add);
  }

  public Span applyTo(@NonNull Span span) {
    accept(span);
    return span;
  }

  @NonNull
  public static Span highlight(@NonNull Span span) {
    PRIMARY_CONTRAST.applyTo(span);
    span.addClassNames(LumoUtility.FontWeight.BLACK);
    return span;
  }

  public Span build() {
    return applyTo(new Span());
  }
}
