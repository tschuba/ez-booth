/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.ui.util;

import static com.vaadin.flow.component.button.ButtonVariant.LUMO_TERTIARY_INLINE;
import static com.vaadin.flow.component.notification.Notification.Position;
import static com.vaadin.flow.theme.lumo.LumoUtility.Padding;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import jakarta.annotation.Nullable;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Duration;
import java.util.List;
import lombok.NonNull;
import org.vaadin.lineawesome.LineAwesomeIcon;
import tschuba.ez.booth.i18n.I18N;
import tschuba.ez.booth.i18n.TranslationKeys;

@SuppressWarnings("unused")
public class Notifications {
  public static final Duration DEFAULT_DURATION = Duration.ofSeconds(5);
  public static final Duration ERROR_DURATION = Duration.ofSeconds(30);
  public static final Position DEFAULT_POSITION = Position.TOP_CENTER;

  @NonNull
  private static Notification createNotification(@NonNull String text, @Nullable Icon icon) {
    Notification notification = new Notification();
    notification.setDuration((int) DEFAULT_DURATION.toMillis());
    notification.setPosition(DEFAULT_POSITION);

    Span textSpan = new Span(text);
    notification.add(textSpan);

    Button closeButton = new Button(LineAwesomeIcon.TIMES_SOLID.create());
    closeButton.addClassNames(Padding.Left.MEDIUM);
    closeButton.addThemeVariants(LUMO_TERTIARY_INLINE);
    closeButton.addClickListener(event -> notification.close());
    notification.add(closeButton);

    HorizontalLayout contentLayout = new HorizontalLayout(textSpan, closeButton);
    if (icon != null) {
      contentLayout.addComponentAsFirst(icon);
    }
    contentLayout.setAlignItems(Alignment.CENTER);
    notification.add(contentLayout);
    return notification;
  }

  @NonNull
  private static Notification createNotification(@NonNull String text) {
    return createNotification(text, null);
  }

  public static void message(String text) {
    createNotification(text).open();
  }

  public static void warning(@NonNull String text) {
    createWarning(text).open();
  }

  public static Notification createWarning(@NonNull String text) {
    Notification notification = createNotification(text, VaadinIcon.EXCLAMATION_CIRCLE_O.create());
    notification.addThemeVariants(NotificationVariant.LUMO_WARNING);
    return notification;
  }

  public static void success(String text) {
    createSuccess(text).open();
  }

  public static Notification createSuccess(@NonNull String text) {
    Notification notification = createNotification(text, VaadinIcon.CHECK_CIRCLE.create());
    notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    return notification;
  }

  public static void error(@NonNull String text) {
    createError(text).open();
  }

  public static Notification createError(@NonNull String text) {
    Notification notification = createNotification(text, VaadinIcon.WARNING.create());
    notification.setDuration((int) ERROR_DURATION.toMillis());
    notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
    return notification;
  }

  public static void error(@NonNull String text, @NonNull Exception ex) {
    createError(text, ex).open();
  }

  public static Notification createError(String text, Exception ex) {
    StringWriter stackWriter = new StringWriter();
    ex.printStackTrace(new PrintWriter(stackWriter));
    String stackTrace = stackWriter.toString();
    String errorMessage = ex.getLocalizedMessage();

    Notification notification = createError(text);

    final VerticalLayout errorDetails = new VerticalLayout();
    errorDetails.add(new Span(errorMessage));
    errorDetails.add(stackTrace);
    errorDetails.setVisible(false);

    Button showError =
        new Button(I18N.translate(TranslationKeys.Notifications.SHOW_ERROR_BUTTON__TEXT));
    showError.addThemeVariants(ButtonVariant.LUMO_ERROR);
    showError.addClickListener(
        _ -> {
          errorDetails.setVisible(true);
          showError.setVisible(false);
        });
    Button hideError =
        new Button(I18N.translate(TranslationKeys.Notifications.HIDE_ERROR_BUTTON__TEXT));
    hideError.addThemeVariants(ButtonVariant.LUMO_ERROR);
    hideError.addClickListener(
        _ -> {
          errorDetails.setVisible(false);
          showError.setVisible(true);
        });
    hideError.setVisible(false);

    List<Component> notificationContent = notification.getChildren().toList();
    if (!notificationContent.isEmpty()
        && notificationContent.getFirst() instanceof HasComponents topBar) {
      topBar.add(showError, hideError);
    }
    notification.add(errorDetails);

    return notification;
  }
}
