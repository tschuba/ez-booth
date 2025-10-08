/* Licensed under MIT

Copyright (c) 2025 Thomas Schulte-Bahrenberg */
package tschuba.ez.booth.ui.components.event;

import static com.vaadin.flow.component.button.ButtonVariant.*;
import static com.vaadin.flow.theme.lumo.LumoUtility.*;
import static tschuba.ez.booth.ui.i18n.TranslationKeys.EventSelectionView.*;
import static tschuba.commons.vaadin.components.Buttons.enableAfterClick;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.router.RouteParameters;
import com.vaadin.flow.theme.lumo.LumoUtility;
import java.util.Objects;
import lombok.Getter;
import org.vaadin.lineawesome.LineAwesomeIcon;
import tschuba.basarix.data.model.Event;
import tschuba.ez.booth.ui.components.ConfirmativeButton;
import tschuba.ez.booth.ui.components.Selectable;
import tschuba.ez.booth.ui.util.RoutingParameters;
import tschuba.ez.booth.ui.views.EventDetailsView;
import tschuba.commons.vaadin.NavigateTo;

@Getter
public class EventListItem extends HorizontalLayout implements Selectable {
    private final Event event;

    public EventListItem(Event event) {
        this.event = event;

        boolean equalsCurrentEvent = EventSelection.get().map(key -> Objects.equals(key, event.getKey())).orElse(false);
        if (equalsCurrentEvent) {
            select();
        } else {
            unselect();
        }

        Span descriptionSpan = new Span();
        descriptionSpan.setText(event.getDescription());
        descriptionSpan.addClassNames(FontSize.LARGE);
        Button description = new Button(event.getDescription());
        description.addThemeVariants(LUMO_TERTIARY_INLINE);
        description.addClickListener(clickEvent -> navigateToEventDetails());
        Tooltip.forComponent(description).setText(getTranslation(INFO_BUTTON__TEXT));

        Button selectButton = new Button();
        selectButton.addThemeVariants(LUMO_LARGE, LUMO_ICON);
        if (equalsCurrentEvent) {
            selectButton.setIcon(LineAwesomeIcon.CHECK_SOLID.create());
            selectButton.addThemeVariants(LUMO_SUCCESS, LUMO_TERTIARY);
            selectButton.removeClassName(Border.ALL);
        } else {
            selectButton.setIcon(LineAwesomeIcon.PERSON_BOOTH_SOLID.create());
            selectButton.addThemeVariants(LUMO_CONTRAST);
            selectButton.addClassName(Border.ALL);
            selectButton.addClickListener(clickEvent -> fireEvent(new SelectionEvent(this, clickEvent.isFromClient(), event)));
            Tooltip.forComponent(selectButton).setText(getTranslation(SELECT_BUTTON__TEXT));
        }
        selectButton.addClassNames(Margin.Right.LARGE, Padding.Horizontal.MEDIUM);

        Button closeButton = new Button(LineAwesomeIcon.LOCK_OPEN_SOLID.create());
        closeButton.addThemeVariants(LUMO_LARGE, LUMO_TERTIARY);
        closeButton.setVisible(!event.isClosed());
        closeButton.setDisableOnClick(true);
        closeButton.addClickListener(enableAfterClick(clickEvent -> fireEvent(new CloseEvent(this, clickEvent.isFromClient(), event))));
        Tooltip.forComponent(closeButton).setText(getTranslation(CLOSE_BUTTON__TEXT));

        Button reopenButton = new Button(LineAwesomeIcon.LOCK_SOLID.create());
        reopenButton.addThemeVariants(LUMO_LARGE, LUMO_TERTIARY, LUMO_ERROR);
        reopenButton.setVisible(event.isClosed());
        reopenButton.setDisableOnClick(true);
        reopenButton.addClickListener(enableAfterClick(clickEvent -> fireEvent(new ReopenEvent(this, clickEvent.isFromClient(), event))));
        Tooltip.forComponent(reopenButton).setText(getTranslation(OPEN_BUTTON__TEXT));

        Button editButton = new Button(LineAwesomeIcon.EDIT.create());
        editButton.addThemeVariants(LUMO_LARGE, LUMO_TERTIARY);
        editButton.addClassNames(Padding.NONE);
        editButton.addClickListener(clickEvent -> fireEvent(new EditEvent(this, clickEvent.isFromClient(), event)));
        editButton.setEnabled(!event.isClosed());
        Tooltip.forComponent(editButton).setText(getTranslation(EDIT_BUTTON__TEXT));

        ConfirmativeButton deleteButton = new ConfirmativeButton(LineAwesomeIcon.TRASH_ALT_SOLID.create());
        deleteButton.addThemeVariants(LUMO_LARGE, LUMO_PRIMARY, LUMO_ERROR);
        deleteButton.addClassNames(Margin.Left.LARGE);
        deleteButton.setEnabled(event.isClosed());
        deleteButton.addConfirmationListener(confirmEvent -> fireEvent(new DeleteEvent(this, confirmEvent.isFromClient(), event)));
        String deleteButtonTextKey = (event.isClosed()) ? DELETE_BUTTON__TEXT : DELETE_BUTTON_DISABLED__TEXT;
        Tooltip.forComponent(deleteButton).setText(getTranslation(deleteButtonTextKey));

        Div actions = new Div(editButton, closeButton, reopenButton, deleteButton);
        actions.addClassNames(Display.FLEX, FlexDirection.ROW, LumoUtility.Gap.SMALL);

        addClassNames(Display.FLEX);
        add(selectButton, description);
        addToEnd(actions);
    }

    private void navigateToEventDetails() {
        RouteParameters routeParams = RoutingParameters.builder().event(this.event.getKey()).build();
        NavigateTo.view(EventDetailsView.class, routeParams).currentWindow();
    }

    public void addSelectionListener(ComponentEventListener<SelectionEvent> listener) {
        this.addListener(SelectionEvent.class, listener);
    }

    public void addEditListener(ComponentEventListener<EditEvent> listener) {
        this.addListener(EditEvent.class, listener);
    }

    public void addCloseListener(ComponentEventListener<CloseEvent> listener) {
        this.addListener(CloseEvent.class, listener);
    }

    public void addReopenListener(ComponentEventListener<ReopenEvent> listener) {
        this.addListener(ReopenEvent.class, listener);
    }

    public void addDeleteListener(ComponentEventListener<DeleteEvent> listener) {
        this.addListener(DeleteEvent.class, listener);
    }

    public static class SelectionEvent extends EventListItemEventBase {
        public SelectionEvent(EventListItem source, boolean fromClient, Event event) {
            super(source, fromClient, event);
        }
    }

    public static class EditEvent extends EventListItemEventBase {
        public EditEvent(EventListItem source, boolean fromClient, Event event) {
            super(source, fromClient, event);
        }
    }

    public static class CloseEvent extends EventListItemEventBase {
        public CloseEvent(EventListItem source, boolean fromClient, Event event) {
            super(source, fromClient, event);
        }
    }

    public static class ReopenEvent extends EventListItemEventBase {
        public ReopenEvent(EventListItem source, boolean fromClient, Event event) {
            super(source, fromClient, event);
        }
    }

    public static class DeleteEvent extends EventListItemEventBase {
        public DeleteEvent(EventListItem source, boolean fromClient, Event event) {
            super(source, fromClient, event);
        }
    }
}
