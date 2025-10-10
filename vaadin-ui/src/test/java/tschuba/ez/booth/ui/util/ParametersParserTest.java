/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.ui.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static tschuba.ez.booth.ui.util.Routing.Parameters.*;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.router.RouteParameters;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tschuba.ez.booth.model.ModelTestValues;
import tschuba.ez.booth.ui.views.CheckoutView;

/**
 * Test class for {@link Routing.Parameters.Parser}.
 */
class ParametersParserTest {

  private static final Class<? extends Component> RETURN_TO_VIEW = CheckoutView.class;

  private Routing.Parameters.Parser parser;
  private Routing.Parameters.Parser parserSpy;

  @BeforeEach
  void setUp() {
    RouteParameters paramsMock = mock(RouteParameters.class);
    parser = Routing.Parameters.parser(paramsMock);
    parserSpy = spy(parser);

    when(paramsMock.get(ROUTE_PARAM__BOOTH_ID)).thenReturn(Optional.of(ModelTestValues.BOOTH_ID));
    when(paramsMock.get(ROUTE_PARAM__VENDOR_ID))
        .thenReturn(Optional.of(String.valueOf(ModelTestValues.VENDOR_ID)));
    when(paramsMock.get(ROUTE_PARAM__PURCHASE_ID))
        .thenReturn(Optional.of(ModelTestValues.PURCHASE_ID));
    when(paramsMock.get(ROUTE_PARAM__RETURN_TO_VIEW))
        .thenReturn(Optional.of(RETURN_TO_VIEW.getName()));
  }

  @Test
  void testBoothIdGetter() {
    assertThat(parser.boothId()).hasValue(ModelTestValues.BOOTH_ID);
  }

  @Test
  void testBoothKeyGetter() {
    assertThat(parser.boothKey())
        .hasValueSatisfying(key -> assertThat(key.boothId()).isEqualTo(ModelTestValues.BOOTH_ID));
  }

  @Test
  void testVendorIdGetter() {
    assertThat(parser.vendorId()).hasValue(ModelTestValues.VENDOR_ID);
  }

  @Test
  void testVendorKeyGetter() {
    assertThat(parser.vendorKey())
        .hasValueSatisfying(
            key -> {
              assertThat(key.booth().boothId()).isEqualTo(ModelTestValues.BOOTH_ID);
              assertThat(key.vendorId()).isEqualTo(ModelTestValues.VENDOR_ID);
            });
  }

  @Test
  void testPurchaseIdGetter() {
    assertThat(parser.purchaseId()).hasValue(ModelTestValues.PURCHASE_ID);
  }

  @Test
  void testPurchaseKeyGetter() {
    assertThat(parser.purchaseKey())
        .hasValueSatisfying(
            key -> {
              assertThat(key.booth().boothId()).isEqualTo(ModelTestValues.BOOTH_ID);
              assertThat(key.purchaseId()).isEqualTo(ModelTestValues.PURCHASE_ID);
            });
  }

  @Test
  void testReturnToView() {
    assertThat(parserSpy.returnToView()).hasValue(RETURN_TO_VIEW);
  }
}
