/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.ui.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static tschuba.ez.booth.ui.util.Routing.Parameters.ROUTE_PARAM__BOOTH_ID;
import static tschuba.ez.booth.ui.util.Routing.Parameters.ROUTE_PARAM__VENDOR_ID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import tschuba.ez.booth.model.DataModel;
import tschuba.ez.booth.model.ModelTestValues;
import tschuba.ez.booth.ui.views.BoothSelectionView;
import tschuba.ez.booth.ui.views.CheckoutView;
import tschuba.ez.booth.ui.views.VendorReportView;

/**
 * Test class for {@link Routing.Parameters.Builder}.
 */
class ParametersBuilderTest {

  private Routing.Parameters.Builder builderSpy;

  @BeforeEach
  void setUp() {
    Routing.Parameters.Builder builder = Routing.Parameters.builder();
    builderSpy = spy(builder);
  }

  @Test
  void testBoothSetter() {
    DataModel.Booth.Key keyMock = mock(DataModel.Booth.Key.class);
    when(keyMock.boothId()).thenReturn(ModelTestValues.BOOTH_ID);

    assertThat(builderSpy.booth(keyMock)).isEqualTo(builderSpy);
    verify(builderSpy).param(ROUTE_PARAM__BOOTH_ID, ModelTestValues.BOOTH_ID);
  }

  @Test
  void testBoothIdSetter() {
    assertThat(builderSpy.boothId(ModelTestValues.BOOTH_ID)).isEqualTo(builderSpy);
    verify(builderSpy).param(ROUTE_PARAM__BOOTH_ID, ModelTestValues.BOOTH_ID);
  }

  @Test
  void testVendorSetter() {
    DataModel.Vendor.Key vendorKeyMock = mock(DataModel.Vendor.Key.class);
    when(vendorKeyMock.booth())
        .thenReturn(DataModel.Booth.Key.builder().boothId(ModelTestValues.BOOTH_ID).build());
    when(vendorKeyMock.vendorId()).thenReturn(ModelTestValues.VENDOR_ID);

    assertThat(builderSpy.vendor(vendorKeyMock)).isEqualTo(builderSpy);
    verify(builderSpy).param(ROUTE_PARAM__VENDOR_ID, ModelTestValues.VENDOR_ID);
  }

  @Test
  void testVendorIdSetter() {
    assertThat(builderSpy.vendorId(ModelTestValues.VENDOR_ID)).isEqualTo(builderSpy);
    verify(builderSpy).param(ROUTE_PARAM__VENDOR_ID, ModelTestValues.VENDOR_ID);
  }

  @Test
  void testPurchaseSetter() {
    DataModel.Booth.Key boothKey =
        DataModel.Booth.Key.builder().boothId(ModelTestValues.BOOTH_ID).build();
    DataModel.Purchase.Key purchaseKeyMock = mock(DataModel.Purchase.Key.class);
    when(purchaseKeyMock.booth()).thenReturn(boothKey);
    when(purchaseKeyMock.purchaseId()).thenReturn(ModelTestValues.PURCHASE_ID);

    assertThat(builderSpy.purchase(purchaseKeyMock)).isEqualTo(builderSpy);
    verify(builderSpy)
        .param(Routing.Parameters.ROUTE_PARAM__PURCHASE_ID, ModelTestValues.PURCHASE_ID);
  }

  @Test
  void testPurchaseIdSetter() {
    assertThat(builderSpy.purchaseId(ModelTestValues.PURCHASE_ID)).isEqualTo(builderSpy);
    verify(builderSpy)
        .param(Routing.Parameters.ROUTE_PARAM__PURCHASE_ID, ModelTestValues.PURCHASE_ID);
  }

  @ParameterizedTest
  @ValueSource(classes = {CheckoutView.class, VendorReportView.class, BoothSelectionView.class})
  @SuppressWarnings({"unchecked", "rawtypes"})
  void testReturnToViewSetter(Class view) {
    assertThat(builderSpy.returnToView(view)).isEqualTo(builderSpy);
    verify(builderSpy).param(Routing.Parameters.ROUTE_PARAM__RETURN_TO_VIEW, view.getName());
  }

  @Test
  void testParamSetter() {
    assertThat(builderSpy.param(ROUTE_PARAM__BOOTH_ID, ModelTestValues.BOOTH_ID))
        .isEqualTo(builderSpy);
  }

  @Test
  void testBuildMethod() {
    assertThat(builderSpy.param(ROUTE_PARAM__BOOTH_ID, ModelTestValues.BOOTH_ID))
        .isEqualTo(builderSpy);
    assertThat(builderSpy.build())
        .satisfies(
            params -> {
              assertThat(params.getParameterNames()).containsOnly(ROUTE_PARAM__BOOTH_ID);
              assertThat(params.get(ROUTE_PARAM__BOOTH_ID)).hasValue(ModelTestValues.BOOTH_ID);
            });
  }
}
