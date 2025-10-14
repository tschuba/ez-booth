/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.ui.util;

import static com.vaadin.flow.component.button.ButtonVariant.LUMO_TERTIARY_INLINE;
import static com.vaadin.flow.component.notification.Notification.Position;
import static com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import static tschuba.ez.booth.i18n.TranslationKeys.Notifications.HIDE_ERROR_BUTTON__TEXT;
import static tschuba.ez.booth.i18n.TranslationKeys.Notifications.SHOW_ERROR_BUTTON__TEXT;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.theme.lumo.LumoUtility.Background;
import com.vaadin.flow.theme.lumo.LumoUtility.BoxShadow;
import jakarta.annotation.Nullable;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Duration;
import java.util.stream.Stream;
import lombok.NonNull;
import org.vaadin.lineawesome.LineAwesomeIcon;
import tschuba.ez.booth.i18n.I18N;

@SuppressWarnings("unused")
public class Notifications {
  public static final Duration DEFAULT_DURATION = Duration.ofSeconds(5);
  public static final Duration ERROR_DURATION = Duration.ofSeconds(0);
  public static final Position DEFAULT_POSITION = Position.TOP_CENTER;

  @NonNull
  private static Button createCloseButton(@NonNull final Notification notification) {
    Button closeButton = new Button(LineAwesomeIcon.TIMES_SOLID.create());
    closeButton.addClassNames(Padding.Left.MEDIUM);
    closeButton.addThemeVariants(LUMO_TERTIARY_INLINE);
    closeButton.addClickListener(_ -> notification.close());
    return closeButton;
  }

  private static HorizontalLayout createContent(@NonNull Component... components) {
    return new HorizontalLayout(Alignment.CENTER, components);
  }

  @NonNull
  private static HorizontalLayout createContent(@NonNull String text, @Nullable Icon icon) {
    Stream.Builder<Component> streamBuilder = Stream.builder();
    if (icon != null) {
      streamBuilder.add(icon);
    }
    streamBuilder.add(new Span(text));
    return createContent(streamBuilder.build().toArray(Component[]::new));
  }

  @NonNull
  private static Notification createNotification() {
    Notification notification = new Notification();
    notification.setDuration((int) DEFAULT_DURATION.toMillis());
    notification.setPosition(DEFAULT_POSITION);
    return notification;
  }

  @NonNull
  private static Notification createNotification(@NonNull Component content) {
    Notification notification = createNotification();

    Button closeButton = new Button(LineAwesomeIcon.TIMES_SOLID.create());
    closeButton.addClassNames(Padding.Left.MEDIUM);
    closeButton.addThemeVariants(LUMO_TERTIARY_INLINE);
    closeButton.addClickListener(event -> notification.close());

    notification.add(new HorizontalLayout(Alignment.CENTER, content, closeButton));
    return notification;
  }

  @NonNull
  private static Notification createNotification(@NonNull String text) {
    return createNotification(createContent(text, null));
  }

  public static void message(String text) {
    createNotification(text).open();
  }

  public static void warning(@NonNull String text) {
    createWarning(text).open();
  }

  @NonNull
  public static Notification createWarning(@NonNull String text) {
    Notification notification =
        createNotification(createContent(text, VaadinIcon.EXCLAMATION_CIRCLE_O.create()));
    notification.addThemeVariants(NotificationVariant.LUMO_WARNING);
    return notification;
  }

  public static void success(String text) {
    createSuccess(text).open();
  }

  @NonNull
  public static Notification createSuccess(@NonNull String text) {
    Notification notification =
        createNotification(createContent(text, VaadinIcon.CHECK_CIRCLE.create()));
    notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    return notification;
  }

  public static void error(@NonNull String text) {
    createError(text).open();
  }

  public static void error(@NonNull String text, @NonNull Exception ex) {
    createError(text, ex).open();
  }

  @NonNull
  public static Notification createError(@NonNull String text) {
    return createError(text, null);
  }

  @NonNull
  public static Notification createError(@NonNull String text, @Nullable Exception ex) {
    Notification notification = createNotification();
    notification.setDuration((int) ERROR_DURATION.toMillis());
    notification.addThemeVariants(NotificationVariant.LUMO_ERROR);

    HorizontalLayout simpleContent = createContent(text, VaadinIcon.WARNING.create());

    if (ex != null) {
      StringWriter stackWriter = new StringWriter();
      ex.printStackTrace(new PrintWriter(stackWriter));
      String stackTrace = stackWriter.toString();
      String errorMessage = ex.getLocalizedMessage();

      final VerticalLayout errorDetails = new VerticalLayout();
      errorDetails.addClassNames(Background.ERROR_50);
      errorDetails.add(new Span(errorMessage));
      errorDetails.add(stackTrace);
      errorDetails.setVisible(false);

      Button showError = new Button(I18N.translate(SHOW_ERROR_BUTTON__TEXT));
      Button hideError = new Button(I18N.translate(HIDE_ERROR_BUTTON__TEXT));
      showError.addClickListener(
          _ -> {
            errorDetails.setVisible(true);
            showError.setVisible(false);
            hideError.setVisible(true);
          });
      hideError.addClickListener(
          _ -> {
            errorDetails.setVisible(false);
            showError.setVisible(true);
            hideError.setVisible(false);
          });
      hideError.setVisible(false);
      Stream.of(showError, hideError)
          .forEach(
              button -> {
                button.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);
                button.addClassNames(Background.ERROR_50, BoxShadow.SMALL);
              });

      simpleContent.add(showError, hideError);

      Scroller scroller = new Scroller(errorDetails);
      scroller.setMaxHeight(30, Unit.REM);
      notification.add(new VerticalLayout(simpleContent, scroller));
    } else {
      notification.add(simpleContent);
    }

    Button closeButton = createCloseButton(notification);
    simpleContent.add(closeButton);

    return notification;
  }
}
