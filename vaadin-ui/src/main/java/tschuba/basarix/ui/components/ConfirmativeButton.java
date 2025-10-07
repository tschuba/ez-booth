package tschuba.basarix.ui.components;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.shared.HasThemeVariant;
import com.vaadin.flow.dom.ThemeList;
import com.vaadin.flow.theme.lumo.LumoUtility.Background;
import com.vaadin.flow.theme.lumo.LumoUtility.TextColor;
import org.vaadin.lineawesome.LineAwesomeIcon;
import tschuba.basarix.common.Args;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Consumer;

import static com.vaadin.flow.component.button.ButtonVariant.LUMO_LARGE;
import static com.vaadin.flow.component.button.ButtonVariant.LUMO_SMALL;
import static com.vaadin.flow.component.menubar.MenuBarVariant.LUMO_ICON;
import static org.vaadin.lineawesome.LineAwesomeIcon.TIMES_SOLID;

public class ConfirmativeButton extends Composite<HorizontalLayout> implements HasEnabled, HasThemeVariant<ButtonVariant> {
    private static final LineAwesomeIcon CONFIRMATION_ICON = LineAwesomeIcon.CHECK_SOLID;
    private static final LineAwesomeIcon CANCEL_ICON = LineAwesomeIcon.BAN_SOLID;

    private final Button actionButton;
    private final MenuBar confirmationMenu = new MenuBar();

    public ConfirmativeButton() {
        super();
        this.actionButton = new Button();
    }

    public ConfirmativeButton(Component actionIcon) {
        this();
        Args.nonNull(actionIcon, "actionIcon");
        this.actionButton.setIcon(actionIcon);
    }

    public ConfirmativeButton(Component actionIcon, String text) {
        this(actionIcon);
        Args.nonNull(text, "text");
    }

    public ConfirmativeButton(String text) {
        this();
        Args.nonNull(text, "text");
        this.actionButton.setText(text);
    }

    @Override
    protected HorizontalLayout initContent() {
        actionButton.addClickListener(this::onClickAction);
        actionButton.setVisible(true);

        confirmationMenu.setVisible(false);
        confirmationMenu.addThemeVariants(LUMO_ICON);
        MenuItem cancelItem = confirmationMenu.addItem(TIMES_SOLID.create(), clickEvent -> onMenuClick(clickEvent, this::onCancellation));
        cancelItem.addClassNames(TextColor.ERROR);
        MenuItem confirmationItem = confirmationMenu.addItem(CONFIRMATION_ICON.create(), clickEvent -> onMenuClick(clickEvent, this::onConfirmation));
        confirmationItem.addClassNames(Background.SUCCESS, TextColor.SUCCESS_CONTRAST);

        HorizontalLayout container = super.initContent();
        container.setSpacing(false);
        container.setAlignItems(Alignment.CENTER);
        container.add(actionButton, confirmationMenu);
        return container;
    }

    @Override
    public ThemeList getThemeNames() {
        return actionButton.getThemeNames();
    }

    @Override
    public void addThemeVariants(ButtonVariant... variants) {
        HasThemeVariant.super.addThemeVariants(variants);

        applyButtonVariants(confirmationMenu::addThemeVariants, variants);
    }

    @Override
    public void removeThemeVariants(ButtonVariant... variants) {
        HasThemeVariant.super.removeThemeVariants(variants);

        applyButtonVariants(confirmationMenu::removeThemeVariants, variants);
    }

    public void addConfirmationListener(ComponentEventListener<ConfirmEvent> listener) {
        this.addListener(ConfirmEvent.class, listener);
    }

    public void addCancelListener(ComponentEventListener<CancelEvent> listener) {
        this.addListener(CancelEvent.class, listener);
    }

    private void onClickAction(ClickEvent<Button> clickEvent) {
        Button button = clickEvent.getSource();
        button.setVisible(false);
        confirmationMenu.setVisible(true);
    }

    private void onMenuClick(ClickEvent<MenuItem> clickEvent, Consumer<ClickEvent<MenuItem>> action) {
        try {
            action.accept(clickEvent);
        } finally {
            reset();
        }
    }

    private void onConfirmation(ClickEvent<MenuItem> clickEvent) {
        fireEvent(new ConfirmEvent(this, clickEvent));
    }

    private void onCancellation(ClickEvent<MenuItem> clickEvent) {
        fireEvent(new CancelEvent(this, clickEvent));
    }

    private void reset() {
        actionButton.setVisible(true);
        confirmationMenu.setVisible(false);
    }

    private static void applyButtonVariants(Consumer<MenuBarVariant> menuBarVariantConsumer, ButtonVariant... buttonVariants) {
        Map<ButtonVariant, MenuBarVariant> variantsMapping = Map.of(LUMO_LARGE, MenuBarVariant.LUMO_LARGE, LUMO_SMALL, MenuBarVariant.LUMO_SMALL);
        Arrays.stream(buttonVariants).filter(variantsMapping::containsKey)
                .map(variantsMapping::get)
                .forEach(menuBarVariantConsumer);
    }

    private abstract static class EventBase extends ComponentEvent<ConfirmativeButton> {
        public EventBase(ConfirmativeButton source, ClickEvent<MenuItem> clickEvent) {
            super(source, Args.nonNull(clickEvent, "clickEvent").isFromClient());
        }
    }

    public static class ConfirmEvent extends EventBase {
        public ConfirmEvent(ConfirmativeButton source, ClickEvent<MenuItem> clickEvent) {
            super(source, clickEvent);
        }
    }

    public static class CancelEvent extends EventBase {
        public CancelEvent(ConfirmativeButton source, ClickEvent<MenuItem> clickEvent) {
            super(source, clickEvent);
        }
    }

}
