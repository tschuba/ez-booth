/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.ui.layouts.app;

import static com.vaadin.flow.theme.lumo.LumoUtility.*;
import static tschuba.ez.booth.i18n.TranslationKeys.App.TITLE;
import static tschuba.ez.booth.i18n.TranslationKeys.AppLayout.EVENT_LINK__TOOLTIP_TEXT;
import static tschuba.ez.booth.i18n.TranslationKeys.AppLayout.TOGGLE_THEME_BUTTON__TOOLTIP_TEXT;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.dom.ThemeList;
import com.vaadin.flow.router.RouteParameters;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.theme.lumo.Lumo;
import java.util.List;
import java.util.Objects;
import org.vaadin.lineawesome.LineAwesomeIcon;
import tschuba.ez.booth.data.BoothRepository;
import tschuba.ez.booth.i18n.TranslationKeys;
import tschuba.ez.booth.model.EntitiesMapper;
import tschuba.ez.booth.ui.components.ToggleButton;
import tschuba.ez.booth.ui.components.event.BoothSelection;
import tschuba.ez.booth.ui.util.Icons;
import tschuba.ez.booth.ui.util.NavigateTo;
import tschuba.ez.booth.ui.util.Routing;
import tschuba.ez.booth.ui.views.BoothDetailsView;
import tschuba.ez.booth.ui.views.BoothSelectionView;

@Tag("basarix-app-layout")
public class CustomAppLayout extends Component implements RouterLayout, HasStyle {
    private final Div subLayout;
    private final Tabs tabs;
    private Component content;

    protected CustomAppLayout(final BoothRepository booths, final List<MainMenuItem> menuItems) {
        Div rootLayout = new Div();
        rootLayout.addClassNames(Display.FLEX, FlexDirection.ROW, Flex.AUTO);
        rootLayout.setHeightFull();
        add(rootLayout);

        tabs = new Tabs();
        if (!menuItems.isEmpty()) {
            tabs.setOrientation(Tabs.Orientation.VERTICAL);
            menuItems.forEach(tabs::add);

            ToggleButton toggleButton =
                    new ToggleButton(Icons.large(LineAwesomeIcon.BARS_SOLID.create()));
            toggleButton.addThemeVariants(
                    ButtonVariant.LUMO_LARGE,
                    ButtonVariant.LUMO_ICON,
                    ButtonVariant.LUMO_TERTIARY,
                    ButtonVariant.LUMO_CONTRAST);
            toggleButton.addClassNames(
                    Padding.XSMALL,
                    Padding.Top.NONE,
                    Margin.XSMALL,
                    Margin.Top.SMALL,
                    Border.BOTTOM,
                    BorderColor.PRIMARY,
                    BorderRadius.NONE);
            toggleButton.addClickListener(this::onClickToggle);
            toggleButton.addToggleListener(event -> updateToggleButton(event.getSource()));
            updateToggleButton(toggleButton);

            Div leftBar = new Div(toggleButton, tabs);
            leftBar.addClassNames(Background.CONTRAST_10);
            rootLayout.add(leftBar);
        }

        subLayout = new Div();
        subLayout.addClassNames(Display.FLEX, FlexDirection.COLUMN, Flex.AUTO);
        rootLayout.add(subLayout);

        HorizontalLayout topBar = new HorizontalLayout();
        topBar.setWidthFull();
        topBar.addClassNames(
                Padding.Horizontal.SMALL,
                Padding.Vertical.XSMALL,
                Border.BOTTOM,
                BorderColor.CONTRAST_10);
        topBar.setAlignItems(FlexComponent.Alignment.CENTER);
        subLayout.add(topBar);

        H1 appName = new H1(getTranslation(TITLE));
        appName.addClassNames(Margin.Vertical.MEDIUM, Margin.End.AUTO, FontSize.LARGE);
        if (menuItems.isEmpty()) {
            appName.addClassNames(Padding.Left.LARGE);
        }
        topBar.add(appName);

        BoothSelection.get()
                .map(EntitiesMapper::objectToEntity)
                .flatMap(booths::findById)
                .ifPresent(
                        event -> {
                            RouterLink eventLink = new RouterLink();
                            eventLink.setRoute(BoothSelectionView.class);
                            eventLink.addClassNames(Margin.Right.MEDIUM);

                            Span descriptionText = new Span(event.getDescription());
                            descriptionText.addClassNames(FontWeight.MEDIUM);
                            eventLink.add(descriptionText);

                            Button detailsButton = new Button();
                            detailsButton.setIcon(LineAwesomeIcon.INFO_CIRCLE_SOLID.create());
                            detailsButton.addClassNames(Padding.NONE);
                            detailsButton.addThemeVariants(ButtonVariant.LUMO_ICON);
                            detailsButton.addClickListener(
                                    _ -> {
                                        RouteParameters routeParams =
                                                Routing.Parameters.builder()
                                                        .booth(
                                                                EntitiesMapper.entityToObject(
                                                                        event.getKey()))
                                                        .build();
                                        NavigateTo.view(BoothDetailsView.class, routeParams)
                                                .currentWindow();
                                    });
                            Tooltip.forComponent(detailsButton)
                                    .setText(
                                            getTranslation(
                                                    TranslationKeys.EventSelectionView
                                                            .INFO_BUTTON__TEXT));

                            Tooltip.forComponent(eventLink)
                                    .withText(getTranslation(EVENT_LINK__TOOLTIP_TEXT))
                                    .withPosition(Tooltip.TooltipPosition.END_BOTTOM);

                            topBar.add(eventLink, detailsButton);
                        });

        Button themeVariantButton = new Button();
        themeVariantButton.setIcon(LineAwesomeIcon.ADJUST_SOLID.create());
        themeVariantButton.addClickListener(
                clickEvent -> {
                    ThemeList themeList = UI.getCurrent().getElement().getThemeList();
                    if (themeList.contains(Lumo.DARK)) {
                        themeList.remove(Lumo.DARK);
                    } else {
                        themeList.add(Lumo.DARK);
                    }
                });
        topBar.add(themeVariantButton);

        Tooltip.forComponent(themeVariantButton)
                .withText(getTranslation(TOGGLE_THEME_BUTTON__TOOLTIP_TEXT));
    }

    private void onClickToggle(ClickEvent<Button> clickEvent) {
        ToggleButton toggleButton = (ToggleButton) clickEvent.getSource();
        toggleButton.toggle();

        tabs.getChildren()
                .map(component -> (MainMenuItem) component)
                .forEach(MainMenuItem::toggleText);
    }

    private void updateToggleButton(ToggleButton button) {
        String[] borderClassNames = {Border.BOTTOM, BorderColor.PRIMARY, BorderRadius.NONE};
        button.removeClassNames(borderClassNames);
        if (!button.isToggled()) {
            button.addClassNames(borderClassNames);
        }
    }

    @Override
    public void showRouterLayoutContent(HasElement content) {
        Component target = null;
        if (content != null) {
            target =
                    content.getElement()
                            .getComponent()
                            .orElseThrow(
                                    () ->
                                            new IllegalArgumentException(
                                                    "AppLayout content must be a Component"));
        }
        setContent(target);
    }

    private void setContent(Component content) {
        removeContent();

        // select menu item matching content
        tabs.getChildren()
                .map(tab -> (MainMenuItem) tab)
                .filter(menuItem -> Objects.equals(menuItem.getView(), content.getClass()))
                .forEach(tabs::setSelectedTab);

        if (content != null) {
            this.content = content;
            subLayout.add(content);
        }
    }

    private void removeContent() {
        remove(this.content);
        this.content = null;
    }

    private void add(Component component) {
        getElement().appendChild(component.getElement());
    }

    private void remove(Component component) {
        if (component != null) {
            component.getElement().removeFromParent();
        }
    }
}
