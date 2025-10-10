/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tschuba.ez.booth.model.DataModel;

/**
 * Service model classes for various service operations.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ServiceModel {

    /**
     * Model for an item in the checkout operation.
     */
    @Builder(toBuilder = true)
    public record CheckoutItem(
            @EqualsAndHashCode.Include DataModel.Vendor.Key vendor,
            BigDecimal price,
            LocalDateTime purchasedOn) {}

    /**
     * Model for checkout operation.
     */
    @Builder(toBuilder = true)
    public record Checkout(
            @NonNull DataModel.Booth.Key booth,
            @NonNull List<ServiceModel.CheckoutItem> items,
            boolean printReceipt) {}

    /**
     * Model for charged fees.
     */
    @Builder(toBuilder = true)
    public record ChargedFees(@NonNull BigDecimal participationFee, @NonNull BigDecimal salesFee) {

        /**
         * Calculate the total fees.
         *
         * @return the total fees
         */
        public BigDecimal total() {
            return participationFee.add(salesFee);
        }
    }

    /**
     * Configuration for charging fees.
     */
    @Builder(toBuilder = true)
    public record ChargingConfig(
            @NonNull BigDecimal participationFee,
            @NonNull BigDecimal salesFee,
            @NonNull BigDecimal roundingStep) {

        private static final Logger LOGGER = LoggerFactory.getLogger(ChargingConfig.class);

        /**
         * Create a {@link ChargingConfig} from the given booth.
         *
         * @param booth the booth to create the config from
         * @return the created config
         */
        public static ChargingConfig of(@NonNull DataModel.Booth booth) {
            return ChargingConfig.builder()
                    .participationFee(booth.participationFee())
                    .salesFee(booth.salesFee())
                    .roundingStep(booth.feesRoundingStep())
                    .build();
        }

        /**
         * Calculate the fees based on the given value.
         *
         * @param value the value to calculate fees for
         * @return the calculated fees
         */
        public ChargedFees calculateFees(@NonNull BigDecimal value) {
            BigDecimal salesFee =
                    this.salesFee()
                            .multiply(value)
                            .scaleByPowerOfTen(-2)
                            .setScale(2, RoundingMode.HALF_UP);
            LOGGER.debug("Calculated sales fee {} for value {}", salesFee, value);
            return ChargedFees.builder()
                    .participationFee(this.participationFee())
                    .salesFee(salesFee)
                    .build();
        }
    }

    /**
     * Model for balance calculation.
     */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Balance {
        /**
         * Input for balance calculation.
         */
        @Builder(toBuilder = true)
        public record Input(
                @NonNull BigDecimal totalSalesAmount,
                @NonNull ServiceModel.ChargingConfig chargingConfig) {}

        /**
         * Output for balance calculation.
         */
        @Builder(toBuilder = true)
        public record Output(@NonNull BigDecimal totalRevenue, @NonNull ChargedFees chargedFees) {}
    }

    /**
     * Input for vendor report generation.
     */
    @Builder(toBuilder = true)
    public record VendorReportInput(@NonNull DataModel.Vendor.Key... vendors) {}

    /**
     * Data for vendor report generation.
     */
    @Builder(toBuilder = true)
    public record VendorReportData(
            @NonNull DataModel.Vendor vendor,
            @NonNull DataModel.Booth booth,
            @NonNull List<DataModel.PurchaseItem> items,
            @NonNull BigDecimal salesSum,
            @NonNull BigDecimal participationFee,
            @NonNull BigDecimal salesFee,
            @NonNull BigDecimal totalRevenue) {}

    /**
     * Data for data exchange operations.
     * @param booth the booth information
     * @param vendors the list of vendors
     * @param purchases the list of purchases
     */
    @Builder(toBuilder = true)
    public record ExchangeData(
            @NonNull DataModel.Booth booth,
            @NonNull List<DataModel.Vendor> vendors,
            @NonNull List<DataModel.Purchase> purchases) {}

    /**
     * Receiver information for data exchange operations.
     * @param name the name of the receiver
     * @param endpoint the endpoint of the receiver
     */
    @Builder(toBuilder = true)
    public record ExchangeReceiver(@NonNull String name, @NonNull String endpoint) {}

    /**
     * Subscription information for data exchange operations.
     * @param id the subscription id
     * @param booth the booth key
     */
    @Builder(toBuilder = true)
    public record ExchangeSubscription(@NonNull String id, @NonNull DataModel.Booth.Key booth) {}
}
