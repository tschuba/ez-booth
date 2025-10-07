/* Licensed under MIT

Copyright (c) 2025 Thomas Schulte-Bahrenberg */
package tschuba.basarix.ui.views;

import static com.vaadin.flow.theme.lumo.LumoUtility.*;
import static tschuba.basarix.ui.i18n.TranslationKeys.EventSelection.NOTIFICATION__NO_EVENT_SELECTED;
import static tschuba.basarix.ui.i18n.TranslationKeys.EventSelectionView.*;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.virtuallist.VirtualList;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.*;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.lineawesome.LineAwesomeIcon;
import tschuba.basarix.data.model.Event;
import tschuba.basarix.data.model.EventKey;
import tschuba.basarix.services.EventService;
import tschuba.basarix.services.dto.CloseEventException;
import tschuba.basarix.services.dto.DeleteEventException;
import tschuba.basarix.services.dto.OpenEventException;
import tschuba.basarix.ui.components.PageTitle;
import tschuba.basarix.ui.components.event.EventListItem;
import tschuba.basarix.ui.components.event.EventSavedEvent;
import tschuba.basarix.ui.components.event.EventSelection;
import tschuba.basarix.ui.components.event.UpsertEventDialog;
import tschuba.basarix.ui.i18n.TranslationKeys;
import tschuba.basarix.ui.layouts.app.AppLayoutWithMenu;
import tschuba.basarix.ui.util.RoutingParameters;
import tschuba.basarix.ui.util.UIUtil;
import tschuba.commons.vaadin.NavigateTo;
import tschuba.commons.vaadin.Notifications;

@Route(value = "event", layout = AppLayoutWithMenu.class)
public class EventSelectionView extends Div implements BeforeEnterObserver, AfterNavigationObserver, HasDynamicTitle {
    private final EventService eventService;
    private final VirtualList<Event> eventList;
    private final UpsertEventDialog editDialog;
    private Class<? extends Component> returnToView;

    public EventSelectionView(@Autowired EventService eventService) {
        this.eventService = eventService;

        addClassNames(Display.FLEX, FlexDirection.COLUMN, Flex.GROW_NONE, Height.FULL);

        Main container = new Main();
        container.addClassNames(Display.GRID, Gap.MEDIUM, AlignItems.START, JustifyContent.CENTER, Margin.Horizontal.AUTO, Padding.Bottom.LARGE, Padding.Horizontal.LARGE);
        add(container);

        editDialog = new UpsertEventDialog(eventService);
        editDialog.addEventSavedListener(this::onEventCreated);
        add(editDialog);

        PageTitle header = new PageTitle();
        header.setText(getTranslation(TranslationKeys.EventSelectionView.TITLE));

        Div headerRow = new Div(header);
        headerRow.addClassNames(Display.FLEX, JustifyContent.START);

        Button createButton = new Button(LineAwesomeIcon.PLUS_SOLID.create());
        createButton.addThemeVariants(ButtonVariant.LUMO_LARGE);
        createButton.setText(getTranslation(TranslationKeys.EventSelectionView.CREATE_BUTTON__TEXT));
        createButton.addClickListener(this::onClickCreate);

        Div actionBar = new Div(createButton);
        actionBar.addClassNames(Display.FLEX, JustifyContent.END, Gap.SMALL);

        eventList = new VirtualList<>();
        eventList.addClassNames(Border.TOP, BorderColor.CONTRAST_50);
        eventList.setMinWidth(44, Unit.EM);
        eventList.setMinHeight(60, Unit.EM);
        eventList.setRenderer(new ComponentRenderer<>(event -> {
            EventListItem listItem = new EventListItem(event);
            listItem.addClassNames(Padding.SMALL, Border.BOTTOM, BorderColor.CONTRAST_50);
            listItem.addSelectionListener(selectionEvent -> onEventSelection(selectionEvent.getEvent()));
            listItem.addEditListener(editEvent -> editDialog.open(editEvent.getEvent()));
            listItem.addCloseListener(closeEvent -> onCloseEvent(closeEvent.getEvent()));
            listItem.addReopenListener(reopenEvent -> onReopenEvent(reopenEvent.getEvent()));
            listItem.addDeleteListener(deleteEvent -> onDeleteEvent(deleteEvent.getEvent()));
            return listItem;
        }));

        container.add(headerRow, actionBar, eventList);

        updateEventListItems();
    }

    private void onClickCreate(ClickEvent<Button> clickEvent) {
        editDialog.open();
    }

    private void onEventCreated(EventSavedEvent createdEvent) {
        updateEventListItems();
    }

    private void onEventSelection(Event event) {
        EventSelection.set(event.getKey());
        Class<? extends Component> targetView = (returnToView != null) ? returnToView : CheckoutView.class;
        if (event.isClosed() && CheckoutView.class.isAssignableFrom(targetView)) {
            RouteParameters routeParams = RoutingParameters.builder().event(event.getKey()).build();
            NavigateTo.view(EventDetailsView.class, routeParams).currentWindow();
        } else {
            NavigateTo.view(targetView).currentWindow();
        }
    }

    private void onCloseEvent(Event event) {
        try {
            eventService.closeEvent(event.getKey());
        } catch (CloseEventException ex) {
            Notifications.error(getTranslation(CLOSE_EVENT_FAILED__MESSAGE), ex);
        } finally {
            updateEventListItems();
        }
    }

    private void onReopenEvent(Event event) {
        try {
            eventService.openEvent(event.getKey());
        } catch (OpenEventException ex) {
            Notifications.error(getTranslation(OPEN_EVENT_FAILED__MESSAGE), ex);
        } finally {
            updateEventListItems();
        }
    }

    private void onDeleteEvent(Event event) {
        try {
            EventKey eventToDelete = event.getKey();
            eventService.deleteEvent(eventToDelete);
            EventSelection.deleted(eventToDelete);
        } catch (DeleteEventException ex) {
            Notifications.error(DELETE_EVENT_FAILED__MESSAGE, ex);
        } finally {
            updateEventListItems();
        }
    }

    private void updateEventListItems() {
        Stream<Event> allEvents = eventService.allEvents().sorted(Comparator.comparing(Event::isClosed).reversed().thenComparing(Event::getDate).reversed());
        eventList.setItems(allEvents);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        this.returnToView = RoutingParameters.parser(event.getRouteParameters()).returnToView().orElse(null);
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        Optional.ofNullable(this.returnToView).ifPresent(view -> Notifications.warning(getTranslation(NOTIFICATION__NO_EVENT_SELECTED)));
    }

    @Override
    public String getPageTitle() {
        return UIUtil.pageTitle(this);
    }
}
