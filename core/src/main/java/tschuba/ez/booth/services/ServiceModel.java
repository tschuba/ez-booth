/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.services;

import java.math.BigDecimal;
import java.util.List;
import lombok.Builder;
import lombok.NonNull;
import tschuba.ez.booth.model.DataModel;

/**
 * Service model classes for various service operations.
 */
public final class ServiceModel {
    private ServiceModel() {}

    /**
     * Model for checkout operation.
     */
    @Builder
    public record Checkout(
            @NonNull DataModel.Booth.Key booth,
            @NonNull List<DataModel.PurchaseItem> items,
            boolean printReceipt) {}

    /**
     * Model for charged fees.
     */
    @Builder
    public record ChargedFees(@NonNull BigDecimal participationFee, @NonNull BigDecimal salesFee) {}

    /**
     * Configuration for charging fees.
     */
    @Builder
    public record ChargingConfig(
            @NonNull BigDecimal participationFee,
            @NonNull BigDecimal salesFee,
            @NonNull BigDecimal roundingStep) {}

    /**
     * Model for balance calculation.
     */
    public static final class Balance {
        private Balance() {}

        /**
         * Input for balance calculation.
         */
        @Builder
        public record Input(
                @NonNull BigDecimal totalSalesAmount,
                @NonNull ServiceModel.ChargingConfig chargingConfig) {}

        /**
         * Output for balance calculation.
         */
        @Builder
        public record Output(@NonNull BigDecimal totalRevenue, @NonNull ChargedFees chargedFees) {}
    }

    /**
     * Input for vendor report generation.
     */
    @Builder
    public record VendorReportInput(@NonNull DataModel.Vendor.Key... vendors) {}

    /**
     * Data for vendor report generation.
     */
    @Builder
    public record VendorReportData(
            @NonNull DataModel.Vendor vendor,
            @NonNull DataModel.Booth booth,
            @NonNull List<DataModel.PurchaseItem> items) {}
}
