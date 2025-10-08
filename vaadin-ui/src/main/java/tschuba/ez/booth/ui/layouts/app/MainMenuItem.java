/* Licensed under MIT

Copyright (c) 2025 Thomas Schulte-Bahrenberg */
package tschuba.ez.booth.ui.layouts.app;

import static com.vaadin.flow.theme.lumo.LumoUtility.*;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.router.RouterLink;
import lombok.Getter;
import tschuba.ez.booth.ui.util.Icons;
import tschuba.commons.vaadin.i18n.I18NTextKey;

public class MainMenuItem extends Tab {

    private final Span text;
    @Getter
    private final Class<? extends Component> view;

    public MainMenuItem(I18NTextKey menuTitle, Component icon, Class<? extends Component> view) {
        RouterLink routerLink = new RouterLink();
        routerLink.addClassNames(Display.FLEX, Gap.XSMALL, Height.MEDIUM, AlignItems.CENTER, Padding.Horizontal.SMALL, Padding.Vertical.MEDIUM, TextColor.BODY);
        this.view = view;
        routerLink.setRoute(this.view);

        String title = getTranslation(menuTitle.getKey());

        text = new Span(title);
        text.setVisible(false);
        text.addClassNames(FontWeight.MEDIUM, FontSize.MEDIUM, Whitespace.NOWRAP);
        Tooltip.forComponent(icon).withText(title);

        routerLink.add(icon, text);
        add(routerLink);
    }

    public void toggleText() {
        boolean visible = text.isVisible();
        text.setVisible(!visible);
    }

    public static MainMenuItem create(I18NTextKey title, SvgIcon icon, Class<? extends Component> view) {
        return new MainMenuItem(title, Icons.large(icon), view);
    }
}
