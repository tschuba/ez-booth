package tschuba.ez.booth.ui.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.HtmlComponent;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.dom.Element;

import java.util.stream.Stream;

import static com.vaadin.flow.theme.lumo.LumoUtility.*;

@Tag(Tag.DIV)
public class Block extends HtmlComponent {
    private final Div titleContainer;
    private final Div contentContainer;

    public Block() {
        titleContainer = new Div();
        titleContainer.addClassNames(FontWeight.MEDIUM, Background.CONTRAST_5, BorderRadius.MEDIUM,
                Padding.Left.SMALL, Padding.Right.SMALL, Padding.Top.XSMALL, Padding.Bottom.XSMALL);

        contentContainer = new Div();
        contentContainer.addClassNames(Padding.SMALL);

        Element thisElement = getElement();
        Stream.of(titleContainer, contentContainer).map(HasElement::getElement).forEach(thisElement::appendChild);
    }

    public void setTitle(String title) {
        titleContainer.setText(title);
    }

    public void setContent(Component... content) {
        this.contentContainer.removeAll();
        this.contentContainer.add(content);
    }

    public void setContent(String text) {
        this.setContent(new Span(text));
    }
}
