/* Licensed under MIT

Copyright (c) 2025 Thomas Schulte-Bahrenberg */
package tschuba.basarix.ui.util;

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
import tschuba.commons.vaadin.components.Badges;

/**
 * Test class for {@link BadgeBuilder}.
 */
class BadgeBuilderTest {

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
        BadgeBuilder.badge().success().accept(spanMock);
        verify(themeListMock).add(Badges.SUCCESS);
    }

    @Test
    void testWarning() {
        BadgeBuilder.badge().warning().accept(spanMock);
        verify(themeListMock).add(Badges.WARNING);
    }

    @Test
    void testError() {
        BadgeBuilder.badge().error().accept(spanMock);
        verify(themeListMock).add(Badges.ERROR);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void testContrast(boolean value) {
        BadgeBuilder.badge().contrast(value).accept(spanMock);
        VerificationMode verificationMode = value ? times(1) : never();
        verify(themeListMock, verificationMode).add(Badges.CONTRAST);
    }

    @Test
    void testContrast() {
        BadgeBuilder.badge().contrast().accept(spanMock);
        verify(themeListMock).add(Badges.CONTRAST);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void testPill(boolean value) {
        BadgeBuilder.badge().pill(value).accept(spanMock);
        VerificationMode verificationMode = value ? times(1) : never();
        verify(themeListMock, verificationMode).add(BadgeBuilder.PILL);
    }

    @Test
    void testPill() {
        BadgeBuilder.badge().pill().accept(spanMock);
        verify(themeListMock).add(BadgeBuilder.PILL);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void testSmall(boolean value) {
        BadgeBuilder.badge().small(value).accept(spanMock);
        VerificationMode verificationMode = value ? times(1) : never();
        verify(themeListMock, verificationMode).add(BadgeBuilder.SMALL);
    }

    @Test
    void testSmall() {
        BadgeBuilder.badge().small().accept(spanMock);
        verify(themeListMock).add(BadgeBuilder.SMALL);
    }

    @Test
    void testBadgeFactoryMethod() {
        assertThatNoException().isThrownBy(() -> assertThat(BadgeBuilder.badge()).isNotNull());
    }

    @Test
    void testPrimaryFactoryMethod() {
        assertThatNoException().isThrownBy(() -> assertThat(BadgeBuilder.primary()).isNotNull());
    }

    @Test
    void testAccept() {
        assertThatNoException().isThrownBy(() -> BadgeBuilder.badge().accept(spanMock));
    }

    @Test
    void testApply() {
        assertThatNoException().isThrownBy(() -> {
            Span actualValue = BadgeBuilder.badge().apply(spanMock);
            assertThat(actualValue).isEqualTo(spanMock);
        });
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void testBuild(boolean primary) {
        BadgeBuilder builder = (primary) ? BadgeBuilder.primary() : BadgeBuilder.badge();
        Span actualSpan = builder.build();
        assertThat(actualSpan).isNotNull().extracting(Span::getElement).extracting(Element::getThemeList).satisfies(themeList -> {
            assertThat(themeList).contains(Badges.BADGE);
            if (primary) {
                assertThat(themeList).contains(BadgeBuilder.PRIMARY);
            } else {
                assertThat(themeList).doesNotContain(BadgeBuilder.PRIMARY);
            }
        });
    }
}
