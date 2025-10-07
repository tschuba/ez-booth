package tschuba.basarix.ui.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;

public class PageTitle extends H2 {
    public PageTitle() {
        super();
        init();
    }

    public PageTitle(Component... components) {
        super(components);
        init();
    }

    public PageTitle(String text) {
        super(text);
        init();
    }

    private void init() {
        addClassNames(Padding.Top.SMALL, Padding.Bottom.LARGE);
    }
}
