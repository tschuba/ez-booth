package tschuba.ez.booth.ui.layouts;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Main;

import static com.vaadin.flow.theme.lumo.LumoUtility.*;

public class TwoColumnLayout extends BaseLayout {
    private final Main contentContainer;
    private Component leftColumn;
    private Component rightColumn;

    public TwoColumnLayout() {
        contentContainer = new Main();
        contentContainer.addClassNames(Display.GRID, Grid.Column.COLUMNS_2, Gap.XLARGE, AlignItems.START, JustifyContent.CENTER);
        super.setContent(contentContainer);

        // add initial placeholders
        setLeftColumn(new Div());
        setRightColumn(new Div());
    }

    public void setLeftColumn(Component content) {
        leftColumn = setColumn(leftColumn, content);
    }

    public void setRightColumn(Component content) {
        rightColumn = setColumn(rightColumn, content);
    }

    public void setContents(Component leftColumn, Component rightColumn) {
        setLeftColumn(leftColumn);
        setRightColumn(rightColumn);
    }

    private Component setColumn(Component oldContent, Component newContent) {
        if (oldContent != null) {
            contentContainer.replace(oldContent, newContent);
        } else {
            contentContainer.add(newContent);
        }
        return newContent;
    }
}
