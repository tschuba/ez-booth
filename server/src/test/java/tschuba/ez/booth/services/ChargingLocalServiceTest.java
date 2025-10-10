/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.mock;

import java.math.BigDecimal;
import java.util.stream.Stream;
import lombok.NonNull;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import tschuba.ez.booth.data.BoothRepository;
import tschuba.ez.booth.data.PurchaseItemRepository;

class ChargingLocalServiceTest {

  private ChargingLocalService chargingService;

  @BeforeEach
  void setUp() {
    chargingService =
        new ChargingLocalService(mock(BoothRepository.class), mock(PurchaseItemRepository.class));
  }

  private static Stream<Arguments> provideCalculateBalanceInput() {
    return Stream.of(
        arguments(
            ServiceModel.Balance.Input.builder()
                .totalSalesAmount(BigDecimal.valueOf(59))
                .chargingConfig(
                    ServiceModel.ChargingConfig.builder()
                        .participationFee(BigDecimal.ONE)
                        .salesFee(BigDecimal.valueOf(15))
                        .roundingStep(BigDecimal.valueOf(0.5))
                        .build())
                .build(),
            BigDecimal.valueOf(49.5),
            BigDecimal.valueOf(9.85)));
  }

  @ParameterizedTest
  @MethodSource("provideCalculateBalanceInput")
  void testCalculateBalance(
      @NonNull ServiceModel.Balance.Input input,
      @NonNull BigDecimal expectedTotalRevenue,
      @NonNull BigDecimal expectedTotalFees) {
    ServiceModel.Balance.Output actualBalance = chargingService.calculateBalance(input);
    assertThat(actualBalance)
        .extracting(ServiceModel.Balance.Output::totalRevenue)
        .asInstanceOf(InstanceOfAssertFactories.BIG_DECIMAL)
        .isEqualByComparingTo(expectedTotalRevenue);
    assertThat(actualBalance)
        .extracting(ServiceModel.Balance.Output::chargedFees)
        .extracting(ServiceModel.ChargedFees::total)
        .asInstanceOf(InstanceOfAssertFactories.BIG_DECIMAL)
        .isEqualByComparingTo(expectedTotalFees);
  }
}
