/* Licensed under MIT

Copyright (c) 2025 Thomas Schulte-Bahrenberg */
package tschuba.basarix.ui.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static tschuba.basarix.ui.util.RoutingParameters.ROUTE_PARAM__EVENT_ID;
import static tschuba.basarix.ui.util.RoutingParameters.ROUTE_PARAM__VENDOR_ID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import tschuba.basarix.data.model.EventKey;
import tschuba.basarix.data.model.PurchaseKey;
import tschuba.basarix.data.model.VendorKey;
import tschuba.basarix.ui.views.CheckoutView;
import tschuba.basarix.ui.views.EventSelectionView;
import tschuba.basarix.ui.views.VendorReportView;

/**
 * Test class for {@link RoutingParameters.Builder}.
 */
class RoutingParametersBuilderTest {

    private static final String EVENT_ID = "eventId";
    private static final int VENDOR_ID = 4711;
    private static final String PURCHASE_ID = "purchaseId";

    private RoutingParameters.Builder builder;
    private RoutingParameters.Builder builderSpy;

    @BeforeEach
    void setUp() {
        builder = RoutingParameters.builder();
        builderSpy = spy(builder);
    }

    @Test
    void testEventSetter() {
        EventKey keyMock = mock(EventKey.class);
        when(keyMock.getId()).thenReturn(EVENT_ID);

        assertThat(builderSpy.event(keyMock)).isEqualTo(builderSpy);
        verify(builderSpy).param(ROUTE_PARAM__EVENT_ID, EVENT_ID);
    }

    @Test
    void testEventIdSetter() {
        assertThat(builderSpy.eventId(EVENT_ID)).isEqualTo(builderSpy);
        verify(builderSpy).param(ROUTE_PARAM__EVENT_ID, EVENT_ID);
    }

    @Test
    void testVendorSetter() {
        VendorKey keyMock = mock(VendorKey.class);
        when(keyMock.getEvent()).thenReturn(EventKey.of(EVENT_ID));
        when(keyMock.getId()).thenReturn(VENDOR_ID);

        assertThat(builderSpy.vendor(keyMock)).isEqualTo(builderSpy);
        verify(builderSpy).param(ROUTE_PARAM__VENDOR_ID, String.valueOf(VENDOR_ID));
    }

    @Test
    void testVendorIdSetter() {
        assertThat(builderSpy.vendorId(VENDOR_ID)).isEqualTo(builderSpy);
        verify(builderSpy).param(ROUTE_PARAM__VENDOR_ID, String.valueOf(VENDOR_ID));
    }

    @Test
    void testPurchaseSetter() {
        PurchaseKey keyMock = mock(PurchaseKey.class);
        when(keyMock.getEvent()).thenReturn(EventKey.of(EVENT_ID));
        when(keyMock.getId()).thenReturn(PURCHASE_ID);

        assertThat(builderSpy.purchase(keyMock)).isEqualTo(builderSpy);
        verify(builderSpy).param(RoutingParameters.ROUTE_PARAM__PURCHASE_ID, PURCHASE_ID);
    }

    @Test
    void testPurchaseIdSetter() {
        assertThat(builderSpy.purchaseId(PURCHASE_ID)).isEqualTo(builderSpy);
        verify(builderSpy).param(RoutingParameters.ROUTE_PARAM__PURCHASE_ID, PURCHASE_ID);
    }

    @ParameterizedTest
    @ValueSource(classes = {CheckoutView.class, VendorReportView.class, EventSelectionView.class})
    @SuppressWarnings({"unchecked", "rawtypes"})
    void testReturnToViewSetter(Class view) {
        assertThat(builderSpy.returnToView(view)).isEqualTo(builderSpy);
        verify(builderSpy).param(RoutingParameters.ROUTE_PARAM__RETURN_TO_VIEW, view.getName());
    }

    @Test
    void testParamSetter() {
        assertThat(builderSpy.param(ROUTE_PARAM__EVENT_ID, EVENT_ID)).isEqualTo(builderSpy);
    }

    @Test
    void testBuildMethod() {
        assertThat(builderSpy.param(ROUTE_PARAM__EVENT_ID, EVENT_ID)).isEqualTo(builderSpy);
        assertThat(builderSpy.build()).satisfies(params -> {
            assertThat(params.getParameterNames()).containsOnly(EVENT_ID);
            assertThat(params.get(ROUTE_PARAM__EVENT_ID)).hasValue(EVENT_ID);
        });
    }
}
