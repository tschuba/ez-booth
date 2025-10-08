package tschuba.ez.booth.ui.components;

import com.vaadin.flow.component.HasStyle;

import static com.vaadin.flow.theme.lumo.LumoUtility.Background;

public interface Selectable extends HasStyle {
    default void select() {
        addClassName(Background.PRIMARY_10);
    }

    default void unselect() {
        removeClassName(Background.PRIMARY_10);
    }

    default boolean isSelected() {
        return hasClassName(Background.PRIMARY_10);
    }
}
