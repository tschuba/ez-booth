/* Licensed under MIT

Copyright (c) 2025 Thomas Schulte-Bahrenberg */
package tschuba.ez.booth.ui.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.Mockito.*;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.dom.ThemeList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.verification.VerificationMode;

/**
 * Test class for {@link tschuba.ez.booth.ui.util.Badges}.
 */
class BadgesTest {

    private Span spanMock;
    private ThemeList themeListMock;

    @BeforeEach
    void setUp() {
        themeListMock = mock(ThemeList.class);
        Element spanElementMock = mock(Element.class);
        when(spanElementMock.getThemeList()).thenReturn(themeListMock);
        spanMock = mock(Span.class);
        when(spanMock.getElement()).thenReturn(spanElementMock);
    }

    @Test
    void testSuccess() {
        tschuba.ez.booth.ui.util.Badges.badge().success().accept(spanMock);
        verify(themeListMock).add(Badges.SUCCESS);
    }

    @Test
    void testWarning() {
        tschuba.ez.booth.ui.util.Badges.badge().warning().accept(spanMock);
        verify(themeListMock).add(Badges.WARNING);
    }

    @Test
    void testError() {
        tschuba.ez.booth.ui.util.Badges.badge().error().accept(spanMock);
        verify(themeListMock).add(Badges.ERROR);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void testContrast(boolean value) {
        tschuba.ez.booth.ui.util.Badges.badge().contrast(value).accept(spanMock);
        VerificationMode verificationMode = value ? times(1) : never();
        verify(themeListMock, verificationMode).add(Badges.CONTRAST);
    }

    @Test
    void testContrast() {
        tschuba.ez.booth.ui.util.Badges.badge().contrast().accept(spanMock);
        verify(themeListMock).add(Badges.CONTRAST);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void testPill(boolean value) {
        tschuba.ez.booth.ui.util.Badges.badge().pill(value).accept(spanMock);
        VerificationMode verificationMode = value ? times(1) : never();
        verify(themeListMock, verificationMode).add(tschuba.ez.booth.ui.util.Badges.PILL);
    }

    @Test
    void testPill() {
        tschuba.ez.booth.ui.util.Badges.badge().pill().accept(spanMock);
        verify(themeListMock).add(tschuba.ez.booth.ui.util.Badges.PILL);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void testSmall(boolean value) {
        tschuba.ez.booth.ui.util.Badges.badge().small(value).accept(spanMock);
        VerificationMode verificationMode = value ? times(1) : never();
        verify(themeListMock, verificationMode).add(tschuba.ez.booth.ui.util.Badges.SMALL);
    }

    @Test
    void testSmall() {
        tschuba.ez.booth.ui.util.Badges.badge().small().accept(spanMock);
        verify(themeListMock).add(tschuba.ez.booth.ui.util.Badges.SMALL);
    }

    @Test
    void testBadgeFactoryMethod() {
        assertThatNoException().isThrownBy(() -> assertThat(tschuba.ez.booth.ui.util.Badges.badge()).isNotNull());
    }

    @Test
    void testPrimaryFactoryMethod() {
        assertThatNoException().isThrownBy(() -> assertThat(tschuba.ez.booth.ui.util.Badges.primary()).isNotNull());
    }

    @Test
    void testAccept() {
        assertThatNoException().isThrownBy(() -> tschuba.ez.booth.ui.util.Badges.badge().accept(spanMock));
    }

    @Test
    void testApplyTo() {
        assertThatNoException().isThrownBy(() -> {
            Span actualValue = tschuba.ez.booth.ui.util.Badges.badge().applyTo(spanMock);
            assertThat(actualValue).isEqualTo(spanMock);
        });
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void testBuild(boolean primary) {
        tschuba.ez.booth.ui.util.Badges builder = (primary) ? tschuba.ez.booth.ui.util.Badges.primary() : tschuba.ez.booth.ui.util.Badges.badge();
        Span actualSpan = builder.build();
        assertThat(actualSpan).isNotNull().extracting(Span::getElement).extracting(Element::getThemeList).satisfies(themeList -> {
            assertThat(themeList).contains(Badges.BADGE);
            if (primary) {
                assertThat(themeList).contains(tschuba.ez.booth.ui.util.Badges.PRIMARY);
            } else {
                assertThat(themeList).doesNotContain(tschuba.ez.booth.ui.util.Badges.PRIMARY);
            }
        });
    }
}
