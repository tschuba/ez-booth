package tschuba.ez.booth.ui.renderer;

import com.vaadin.flow.component.UI;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.function.Function;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public abstract class HasUI {
    private final UI ui;

    static <T extends HasUI> T of(Optional<UI> ui, Function<UI, T> creator) {
        return creator.apply(ui.orElseGet(UI::getCurrent));
    }
}
