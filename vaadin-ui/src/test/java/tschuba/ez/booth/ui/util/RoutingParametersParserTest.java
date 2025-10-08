/* Licensed under MIT

Copyright (c) 2025 Thomas Schulte-Bahrenberg */
package tschuba.ez.booth.ui.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static tschuba.ez.booth.ui.util.RoutingParameters.*;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.router.RouteParameters;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tschuba.ez.booth.ui.views.CheckoutView;

/**
 * Test class for {@link RoutingParameters.Parser}.
 */
class RoutingParametersParserTest {

    private static final String EVENT_ID = "eventId";
    private static final int VENDOR_ID = 4711;
    private static final String PURCHASE_ID = "purchaseId";
    private static final Class<? extends Component> RETURN_TO_VIEW = CheckoutView.class;

    private RouteParameters paramsMock;
    private RoutingParameters.Parser parser;
    private RoutingParameters.Parser parserSpy;

    @BeforeEach
    void setUp() {
        paramsMock = mock(RouteParameters.class);
        parser = RoutingParameters.parser(paramsMock);
        parserSpy = spy(parser);

        when(paramsMock.get(ROUTE_PARAM__EVENT_ID)).thenReturn(Optional.of(EVENT_ID));
        when(paramsMock.get(ROUTE_PARAM__VENDOR_ID)).thenReturn(Optional.of(String.valueOf(VENDOR_ID)));
        when(paramsMock.get(ROUTE_PARAM__PURCHASE_ID)).thenReturn(Optional.of(PURCHASE_ID));
        when(paramsMock.get(ROUTE_PARAM__RETURN_TO_VIEW)).thenReturn(Optional.of(RETURN_TO_VIEW.getName()));
    }

    @Test
    void testEventIdGetter() {
        assertThat(parser.eventId()).hasValue(EVENT_ID);
    }

    @Test
    void testEventKeyGetter() {
        assertThat(parser.eventKey()).hasValueSatisfying(key -> assertThat(key.getId()).isEqualTo(EVENT_ID));
    }

    @Test
    void testVendorIdGetter() {
        assertThat(parser.vendorId()).hasValue(VENDOR_ID);
    }

    @Test
    void testVendorKeyGetter() {
        assertThat(parser.vendorKey()).hasValueSatisfying(key -> {
            assertThat(key.getEvent().getId()).isEqualTo(EVENT_ID);
            assertThat(key.getId()).isEqualTo(VENDOR_ID);
        });
    }

    @Test
    void testPurchaseIdGetter() {
        assertThat(parser.purchaseId()).hasValue(PURCHASE_ID);
    }

    @Test
    void testPurchaseKeyGetter() {
        assertThat(parser.purchaseKey()).hasValueSatisfying(key -> {
            assertThat(key.getEvent().getId()).isEqualTo(EVENT_ID);
            assertThat(key.getId()).isEqualTo(PURCHASE_ID);
        });
    }

    @Test
    void testReturnToView() {
        assertThat(parserSpy.returnToView()).hasValue(RETURN_TO_VIEW);
    }
}
