package tschuba.ez.booth.ui.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Duration;

import org.vaadin.lineawesome.LineAwesomeIcon;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import static com.vaadin.flow.component.button.ButtonVariant.LUMO_TERTIARY_INLINE;
import static com.vaadin.flow.component.notification.Notification.Position;
import static com.vaadin.flow.theme.lumo.LumoUtility.Padding;

@SuppressWarnings("unused")
public class Notifications {
    public static final Duration DEFAULT_DURATION = Duration.ofSeconds(5);
    public static final Position DEFAULT_POSITION = Position.TOP_CENTER;

    public static void message(String text) {
        createMessage(text).open();
    }

    public static Notification createMessage(String text) {
        Notification notification = new Notification();
        notification.setDuration((int) DEFAULT_DURATION.toMillis());
        notification.setPosition(DEFAULT_POSITION);

        Span messageSpan = new Span(text);
        notification.add(messageSpan);

        Button closeButton = new Button(LineAwesomeIcon.TIMES_SOLID.create());
        closeButton.addClassNames(Padding.Left.MEDIUM);
        closeButton.addThemeVariants(LUMO_TERTIARY_INLINE);
        closeButton.addClickListener(event -> notification.close());
        notification.add(closeButton);

        notification.add(new VerticalLayout(new Div(messageSpan, closeButton)));

        return notification;
    }

    public static void warning(String text) {
        createWarning(text).open();
    }

    public static Notification createWarning(String text) {
        Notification notification = createMessage(text);
        notification.addThemeVariants(NotificationVariant.LUMO_WARNING);
        return notification;
    }

    public static void success(String text) {
        createSuccess(text).open();
    }

    public static Notification createSuccess(String text) {
        Notification notification = createMessage(text);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        return notification;
    }

    public static void error(String text) {
        createError(text).open();
    }

    public static Notification createError(String text) {
        Notification notification = createMessage(text);
        notification.setDuration(0);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        return notification;
    }

    public static void error(String text, Exception ex) {
        createError(text, ex).open();
    }

    public static Notification createError(String text, Exception ex) {
        StringWriter stackWriter = new StringWriter();
        ex.printStackTrace(new PrintWriter(stackWriter));
        String stackTrace = stackWriter.toString();
        String errorMessage = ex.getLocalizedMessage();

        Notification notification = createError(text);
        // TODO: localise
        Details details = new Details("Details", new Div(new Span(errorMessage), new Span(stackTrace)));
        notification.getChildren().findFirst()
                .filter(child -> child instanceof HasComponents)
                .ifPresent(content -> {
                    ((HasComponents) content).add(details);
                });
        return notification;
    }
}