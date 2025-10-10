/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.ui.views;

import static com.vaadin.flow.theme.lumo.LumoUtility.*;
import static tschuba.ez.booth.i18n.TranslationKeys.EventSelection.NOTIFICATION__NO_EVENT_SELECTED;
import static tschuba.ez.booth.i18n.TranslationKeys.EventSelectionView.*;

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
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.lineawesome.LineAwesomeIcon;
import tschuba.ez.booth.model.DataModel;
import tschuba.ez.booth.services.BoothService;
import tschuba.ez.booth.ui.components.PageTitle;
import tschuba.ez.booth.ui.components.event.BoothListItem;
import tschuba.ez.booth.ui.components.event.BoothSavedEvent;
import tschuba.ez.booth.ui.components.event.BoothSelection;
import tschuba.ez.booth.ui.components.event.UpsertEventDialog;
import tschuba.ez.booth.ui.layouts.app.AppLayoutWithMenu;
import tschuba.ez.booth.ui.util.*;

@Route(value = "booth", layout = AppLayoutWithMenu.class)
@SpringComponent
@UIScope
public class BoothSelectionView extends Div
        implements BeforeEnterObserver, AfterNavigationObserver, HasDynamicTitle {
    private final BoothService boothService;
    private final VirtualList<DataModel.Booth> boothList;
    private final UpsertEventDialog editDialog;
    private Class<? extends Component> returnToView;

    public BoothSelectionView(@NonNull @Autowired BoothService boothService) {
        this.boothService = boothService;

        addClassNames(Display.FLEX, FlexDirection.COLUMN, Flex.GROW_NONE, Height.FULL);

        Main container = new Main();
        container.addClassNames(
                Display.GRID,
                Gap.MEDIUM,
                AlignItems.START,
                JustifyContent.CENTER,
                Margin.Horizontal.AUTO,
                Padding.Bottom.LARGE,
                Padding.Horizontal.LARGE);
        add(container);

        editDialog = new UpsertEventDialog(boothService);
        editDialog.addEventSavedListener(this::onEventCreated);
        add(editDialog);

        PageTitle header = new PageTitle();
        header.setText(getTranslation(TITLE));

        Div headerRow = new Div(header);
        headerRow.addClassNames(Display.FLEX, JustifyContent.START);

        Button createButton = new Button(LineAwesomeIcon.PLUS_SOLID.create());
        createButton.addThemeVariants(ButtonVariant.LUMO_LARGE);
        createButton.setText(getTranslation(CREATE_BUTTON__TEXT));
        createButton.addClickListener(this::onClickCreate);

        Div actionBar = new Div(createButton);
        actionBar.addClassNames(Display.FLEX, JustifyContent.END, Gap.SMALL);

        boothList = new VirtualList<>();
        boothList.addClassNames(Border.TOP, BorderColor.CONTRAST_50);
        boothList.setMinWidth(44, Unit.EM);
        boothList.setMinHeight(60, Unit.EM);
        boothList.setRenderer(
                new ComponentRenderer<>(
                        event -> {
                            BoothListItem listItem = new BoothListItem(event);
                            listItem.addClassNames(
                                    Padding.SMALL, Border.BOTTOM, BorderColor.CONTRAST_50);
                            listItem.addSelectionListener(
                                    selectionEvent -> onBoothSelection(selectionEvent.getBooth()));
                            listItem.addEditListener(
                                    editEvent -> editDialog.open(editEvent.getBooth()));
                            listItem.addCloseListener(
                                    closeEvent -> onCloseBooth(closeEvent.getBooth()));
                            listItem.addReopenListener(
                                    reopenEvent -> onReopenBooth(reopenEvent.getBooth()));
                            listItem.addDeleteListener(
                                    deleteEvent -> onDeleteBooth(deleteEvent.getBooth()));
                            return listItem;
                        }));

        container.add(headerRow, actionBar, boothList);

        updateBoothListItems();
    }

    private void onClickCreate(ClickEvent<Button> clickEvent) {
        editDialog.open();
    }

    private void onEventCreated(BoothSavedEvent createdEvent) {
        updateBoothListItems();
    }

    private void onBoothSelection(DataModel.Booth booth) {
        BoothSelection.set(booth.key());
        Class<? extends Component> targetView =
                (returnToView != null) ? returnToView : CheckoutView.class;
        if (booth.closed() && CheckoutView.class.isAssignableFrom(targetView)) {
            RouteParameters routeParams = Routing.Parameters.builder().booth(booth.key()).build();
            NavigateTo.view(BoothDetailsView.class, routeParams).currentWindow();
        } else {
            NavigateTo.view(targetView).currentWindow();
        }
    }

    private void onCloseBooth(DataModel.Booth booth) {
        try {
            boothService.close(booth.key());
        } catch (Exception ex) {
            Notifications.error(getTranslation(CLOSE_EVENT_FAILED__MESSAGE), ex);
        } finally {
            updateBoothListItems();
        }
    }

    private void onReopenBooth(DataModel.Booth booth) {
        try {
            boothService.open(booth.key());
        } catch (Exception ex) {
            Notifications.error(getTranslation(OPEN_EVENT_FAILED__MESSAGE), ex);
        } finally {
            updateBoothListItems();
        }
    }

    private void onDeleteBooth(DataModel.Booth booth) {
        try {
            DataModel.Booth.Key boothToDelete = booth.key();
            boothService.delete(boothToDelete);
            BoothSelection.deleted(boothToDelete);
        } catch (Exception ex) {
            Notifications.error(DELETE_EVENT_FAILED__MESSAGE, ex);
        } finally {
            updateBoothListItems();
        }
    }

    private void updateBoothListItems() {
        Stream<DataModel.Booth> allBooths =
                boothService
                        .findAll()
                        .sorted(
                                Comparator.comparing(DataModel.Booth::closed)
                                        .reversed()
                                        .thenComparing(DataModel.Booth::date)
                                        .reversed());
        boothList.setItems(allBooths);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        this.returnToView =
                Routing.Parameters.parser(event.getRouteParameters()).returnToView().orElse(null);
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        Optional.ofNullable(this.returnToView)
                .ifPresent(
                        view ->
                                Notifications.warning(
                                        getTranslation(NOTIFICATION__NO_EVENT_SELECTED)));
    }

    @Override
    public String getPageTitle() {
        return UIUtil.pageTitle(this);
    }
}
