/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.ui.components.event;

import static com.vaadin.flow.component.button.ButtonVariant.*;
import static com.vaadin.flow.theme.lumo.LumoUtility.*;
import static tschuba.ez.booth.ui.util.Buttons.enableAfterClick;

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
import tschuba.ez.booth.model.DataModel;
import tschuba.ez.booth.ui.components.ConfirmativeButton;
import tschuba.ez.booth.ui.components.Selectable;
import tschuba.ez.booth.ui.util.NavigateTo;
import tschuba.ez.booth.ui.util.Routing;
import tschuba.ez.booth.ui.views.BoothDetailsView;

@Getter
public class BoothListItem extends HorizontalLayout implements Selectable {
    private final DataModel.Booth booth;

    public BoothListItem(DataModel.Booth booth) {
        this.booth = booth;

        boolean equalsCurrentEvent =
                BoothSelection.get().map(key -> Objects.equals(key, booth.key())).orElse(false);
        if (equalsCurrentEvent) {
            select();
        } else {
            unselect();
        }

        Span descriptionSpan = new Span();
        descriptionSpan.setText(booth.description());
        descriptionSpan.addClassNames(FontSize.LARGE);
        Button description = new Button(booth.description());
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
            selectButton.addClickListener(
                    clickEvent ->
                            fireEvent(new SelectionEvent(this, clickEvent.isFromClient(), booth)));
            Tooltip.forComponent(selectButton).setText(getTranslation(SELECT_BUTTON__TEXT));
        }
        selectButton.addClassNames(Margin.Right.LARGE, Padding.Horizontal.MEDIUM);

        Button closeButton = new Button(LineAwesomeIcon.LOCK_OPEN_SOLID.create());
        closeButton.addThemeVariants(LUMO_LARGE, LUMO_TERTIARY);
        closeButton.setVisible(!booth.closed());
        closeButton.setDisableOnClick(true);
        closeButton.addClickListener(
                enableAfterClick(
                        clickEvent ->
                                fireEvent(new CloseEvent(this, clickEvent.isFromClient(), booth))));
        Tooltip.forComponent(closeButton).setText(getTranslation(CLOSE_BUTTON__TEXT));

        Button reopenButton = new Button(LineAwesomeIcon.LOCK_SOLID.create());
        reopenButton.addThemeVariants(LUMO_LARGE, LUMO_TERTIARY, LUMO_ERROR);
        reopenButton.setVisible(booth.closed());
        reopenButton.setDisableOnClick(true);
        reopenButton.addClickListener(
                enableAfterClick(
                        clickEvent ->
                                fireEvent(
                                        new ReopenEvent(this, clickEvent.isFromClient(), booth))));
        Tooltip.forComponent(reopenButton).setText(getTranslation(OPEN_BUTTON__TEXT));

        Button editButton = new Button(LineAwesomeIcon.EDIT.create());
        editButton.addThemeVariants(LUMO_LARGE, LUMO_TERTIARY);
        editButton.addClassNames(Padding.NONE);
        editButton.addClickListener(
                clickEvent -> fireEvent(new EditEvent(this, clickEvent.isFromClient(), booth)));
        editButton.setEnabled(!booth.closed());
        Tooltip.forComponent(editButton).setText(getTranslation(EDIT_BUTTON__TEXT));

        ConfirmativeButton deleteButton =
                new ConfirmativeButton(LineAwesomeIcon.TRASH_ALT_SOLID.create());
        deleteButton.addThemeVariants(LUMO_LARGE, LUMO_PRIMARY, LUMO_ERROR);
        deleteButton.addClassNames(Margin.Left.LARGE);
        deleteButton.setEnabled(booth.closed());
        deleteButton.addConfirmationListener(
                confirmEvent ->
                        fireEvent(new DeleteEvent(this, confirmEvent.isFromClient(), booth)));
        String deleteButtonTextKey =
                (booth.closed()) ? DELETE_BUTTON__TEXT : DELETE_BUTTON_DISABLED__TEXT;
        Tooltip.forComponent(deleteButton).setText(getTranslation(deleteButtonTextKey));

        Div actions = new Div(editButton, closeButton, reopenButton, deleteButton);
        actions.addClassNames(Display.FLEX, FlexDirection.ROW, LumoUtility.Gap.SMALL);

        addClassNames(Display.FLEX);
        add(selectButton, description);
        addToEnd(actions);
    }

    private void navigateToEventDetails() {
        RouteParameters routeParams = Routing.Parameters.builder().booth(this.booth.key()).build();
        NavigateTo.view(BoothDetailsView.class, routeParams).currentWindow();
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

    public static class SelectionEvent extends BoothListItemEventBase {
        public SelectionEvent(BoothListItem source, boolean fromClient, DataModel.Booth booth) {
            super(source, fromClient, booth);
        }
    }

    public static class EditEvent extends BoothListItemEventBase {
        public EditEvent(BoothListItem source, boolean fromClient, DataModel.Booth booth) {
            super(source, fromClient, booth);
        }
    }

    public static class CloseEvent extends BoothListItemEventBase {
        public CloseEvent(BoothListItem source, boolean fromClient, DataModel.Booth booth) {
            super(source, fromClient, booth);
        }
    }

    public static class ReopenEvent extends BoothListItemEventBase {
        public ReopenEvent(BoothListItem source, boolean fromClient, DataModel.Booth booth) {
            super(source, fromClient, booth);
        }
    }

    public static class DeleteEvent extends BoothListItemEventBase {
        public DeleteEvent(BoothListItem source, boolean fromClient, DataModel.Booth booth) {
            super(source, fromClient, booth);
        }
    }
}
