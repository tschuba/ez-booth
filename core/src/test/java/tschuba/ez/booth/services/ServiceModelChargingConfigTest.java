/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.math.BigDecimal;
import java.util.stream.Stream;
import lombok.NonNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class ServiceModelChargingConfigTest {

    private static Stream<Arguments> provideCalculateFeesInput() {
        return Stream.of(
                arguments(
                        ServiceModel.ChargingConfig.builder()
                                .salesFee(BigDecimal.valueOf(15))
                                .participationFee(BigDecimal.ONE)
                                .roundingStep(BigDecimal.valueOf(0.5))
                                .build(),
                        BigDecimal.valueOf(59),
                        BigDecimal.valueOf(9.85)));
    }

    @ParameterizedTest
    @MethodSource("provideCalculateFeesInput")
    void testCalculateFees(
            @NonNull ServiceModel.ChargingConfig config,
            @NonNull BigDecimal revenue,
            @NonNull BigDecimal expectedTotal) {
        ServiceModel.ChargedFees calculatedFees = config.calculateFees(revenue);
        assertThat(calculatedFees.total()).isEqualByComparingTo(expectedTotal);
    }
}
