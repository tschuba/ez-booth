package tschuba.ez.booth.ui.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.theme.lumo.LumoUtility;

public class SectionTitle extends H3 {
    public SectionTitle() {
        init();
    }

    public SectionTitle(Component... components) {
        super(components);
        init();
    }

    public SectionTitle(String text) {
        super(text);
        init();
    }

    private void init() {
        addClassNames(LumoUtility.Padding.Top.SMALL, LumoUtility.Padding.Bottom.LARGE);
    }
}
