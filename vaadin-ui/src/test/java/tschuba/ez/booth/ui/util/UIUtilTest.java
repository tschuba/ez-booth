/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.ui.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.dom.Style;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tschuba.ez.booth.i18n.TranslationKeys;

/**
 * Test class for {@link UIUtil}.
 */
class UIUtilTest {

    private Component componentMock;

    @BeforeEach
    void setUp() {
        componentMock = mock(Component.class);
    }

    @Test
    void testTraverseParents() {
        Component parentOfParentMock = mock(Component.class);
        when(parentOfParentMock.getParent()).thenReturn(Optional.empty());
        Component parentMock = mock(Component.class);
        when(parentMock.getParent()).thenReturn(Optional.of(parentOfParentMock));
        when(componentMock.getParent()).thenReturn(Optional.of(parentMock));

        @SuppressWarnings("unchecked")
        Function<Component, Boolean> traversalFunctionMock = mock(Function.class);
        when(traversalFunctionMock.apply(any())).thenReturn(Boolean.TRUE);

        UIUtil.traverseParents(componentMock, traversalFunctionMock);

        verify(traversalFunctionMock, never()).apply(componentMock);
        verify(traversalFunctionMock).apply(parentMock);
        verify(traversalFunctionMock).apply(parentOfParentMock);
    }

    @Test
    void testTraverseAllOfTypeFromComponent() {
        Span childOfSpanMock = mock(Span.class);
        Span spanMock = mock(Span.class);
        when(spanMock.getChildren()).thenReturn(Stream.of(childOfSpanMock));

        @SuppressWarnings("unchecked")
        Consumer<Span> consumerMock = mock(Consumer.class);
        UIUtil.traverseAllOfType(spanMock, Span.class, consumerMock);

        verify(consumerMock).accept(spanMock);
        verify(consumerMock).accept(childOfSpanMock);
    }

    @Test
    void testTraverseAllOfTypeFromStream() {
        Span childOfSpanMock = mock(Span.class);
        Span spanMock = mock(Span.class);
        when(spanMock.getChildren()).thenReturn(Stream.of(childOfSpanMock));

        @SuppressWarnings("unchecked")
        Consumer<Span> consumerMock = mock(Consumer.class);
        UIUtil.traverseAllOfType(
                Stream.of(mock(Button.class), spanMock, componentMock), Span.class, consumerMock);

        verify(consumerMock).accept(spanMock);
        verify(consumerMock).accept(childOfSpanMock);
    }

    @Test
    void testOptimizeViewForPrinting() {
        Style styleMock = mock(Style.class);
        when(componentMock.getStyle()).thenReturn(styleMock);

        UIUtil.optimizeViewForPrinting(componentMock);

        verify(styleMock).setColor("black");
        verify(styleMock).set("border-color", "black");
    }

    @Test
    void testPageTitle() {
        Component componentMock = mock(Component.class);
        String appTitle = "Some App Title";
        String viewTitle = "Some View Title";
        when(componentMock.getTranslation(TranslationKeys.App.TITLE)).thenReturn(appTitle);
        when(componentMock.getTranslation(
                        "%s.title".formatted(componentMock.getClass().getSimpleName())))
                .thenReturn(viewTitle);

        String actualTitle = UIUtil.pageTitle(componentMock);
        assertThat(actualTitle).isEqualTo("%s - %s".formatted(appTitle, viewTitle));
    }
}
